'use strict';

angular.module('admin').controller(
        'Admin.LdapUserManagementController',
        [
                '$scope',
                '$q',
                '$modal',
                '$timeout',
                'Admin.LdapUserManagementService',
                'LookupService',
                'MessageService',
                'Acm.StoreService',
                'UtilService',
                '$log',
                '$translate',
                function($scope, $q, $modal, $timeout, LdapUserManagementService, LookupService, MessageService, Store, Util, $log,
                        $translate) {

                    $scope.cloneUser = cloneUser;
                    $scope.onObjSelect = onObjSelect;
                    $scope.onAuthRoleSelected = onAuthRoleSelected;
                    $scope.initUser = initUser;
                    $scope.fillList = fillList;
                    $scope.deleteUser = deleteUser;
                    //scroll functions
                    $scope.userScroll = userScroll;
                    $scope.unauthorizedScroll = unauthorizedScroll;
                    $scope.authorizedScroll = authorizedScroll;
                    $scope.retrieveDataScroll = retrieveDataScroll;
                    //filter functions
                    $scope.userManagementFilter = userManagementFilter;
                    $scope.userUnauthorizedFilter = userUnauthorizedFilter;
                    $scope.userAuthorizedFilter = userAuthorizedFilter;
                    $scope.retrieveDataFilter = retrieveDataFilter;
                    $scope.selectFirst = selectFirst;

                    $scope.showFilter = true;
                    var makePaginationRequest = true;
                    var currentAuthGroups;
                    var selectedUser;
                    var objectTitle = $translate.instant('admin.security.ldap.user.management.user');
                    var lastSelectedUser = "";
                    $scope.userData = {
                        "chooseObject" : [],
                        "selectedNotAuthorized" : [],
                        "selectedAuthorized" : []
                    };
                    $scope.scrollLoadData = {
                        "loadObjectsScroll" : $scope.userScroll,
                        "loadUnauthorizedScroll" : $scope.unauthorizedScroll,
                        "loadAuthorizedScroll" : $scope.authorizedScroll
                    };
                    $scope.filterData = {
                        "objectsFilter" : $scope.userManagementFilter,
                        "unauthorizedFilter" : $scope.userUnauthorizedFilter,
                        "authorizedFilter" : $scope.userAuthorizedFilter
                    };

                    function selectFirst(data, flag) {
                        $scope.selectData = {
                            "resetSelect" : flag,
                            "data" : data
                        };
                        $scope.onObjSelect($scope.selectData.data, $scope.userData.selectedAuthorized,
                                $scope.userData.selectedNotAuthorized);
                    }

                    function initUser(userNumber) {
                        var userRequestInfo = {};
                        userRequestInfo.n = Util.isEmpty(userNumber) ? 50 : userNumber;
                        userRequestInfo.start = Util.isEmpty(userNumber) ? 0 : $scope.userData.chooseObject.length - 1;
                        if (makePaginationRequest) {
                            LdapUserManagementService.getNUsers(userRequestInfo).then(function(response) {
                                $scope.fillList($scope.userData.chooseObject, response.data.response.docs);
                                $scope.selectFirst($scope.userData.chooseObject[0], true);
                                makePaginationRequest = response.data.response.numFound > userRequestInfo.n;
                            });
                        }
                    }

                    $scope.initUser();

                    //callback function when user is selected
                    function onObjSelect(selectedObject, authorized, notAuthorized) {
                        var data = {};
                        data.member_id = selectedObject;
                        lastSelectedUser = selectedObject;
                        selectedUser = selectedObject;
                        currentAuthGroups = [];
                        $scope.userData.selectedAuthorized = [];
                        $scope.userData.selectedNotAuthorized = [];

                        data.isAuthorized = false;
                        var unAuthorizedGroupsForUser = LdapUserManagementService.getGroupsForUser(data);
                        data.isAuthorized = true;
                        var authorizedGroupsForUser = LdapUserManagementService.getGroupsForUser(data);

                        $q.all([ authorizedGroupsForUser, unAuthorizedGroupsForUser ]).then(function(result) {
                            _.forEach(result[0].data.response.docs, function(group) {
                                var authObject = {};
                                authObject.key = group.name;
                                authObject.name = group.name;
                                $scope.userData.selectedAuthorized.push(authObject);
                                currentAuthGroups.push(authObject.key);
                            });
                            _.forEach(result[1].data.response.docs, function(group) {
                                var authObject = {};
                                authObject.key = group.name;
                                authObject.name = group.name;
                                $scope.userData.selectedNotAuthorized.push(authObject);
                            });
                        });
                    }

                    //callback function when groups are moved
                    function onAuthRoleSelected(selectedObject, authorized, notAuthorized) {
                        var toBeAdded = [];
                        var toBeRemoved = [];
                        var deferred = $q.defer();

                        //get roles which needs to be added
                        _.forEach(authorized, function(group) {
                            if (currentAuthGroups.indexOf(group.key) === -1) {
                                toBeAdded.push(group.key);
                            }
                        });
                        _.forEach(notAuthorized, function(group) {
                            if (currentAuthGroups.indexOf(group.key) !== -1) {
                                toBeRemoved.push(group.key);
                            }
                        });
                        //perform adding on server
                        if (toBeAdded.length > 0) {
                            currentAuthGroups = currentAuthGroups.concat(toBeAdded);

                            LdapUserManagementService.addGroupsToUser(selectedObject.key, toBeAdded, selectedObject.directory).then(
                                    function(data) {
                                        MessageService.succsessAction();
                                    }, function() {
                                        //error adding group
                                        MessageService.errorAction();
                                    });
                            return deferred.promise;
                        }

                        if (toBeRemoved.length > 0) {
                            _.forEach(toBeRemoved, function(element) {
                                currentAuthGroups.splice(currentAuthGroups.indexOf(element), 1);
                            });

                            LdapUserManagementService.removeGroupsFromUser(selectedObject.key, toBeRemoved, selectedObject.directory).then(
                                    function(data) {
                                        MessageService.succsessAction();
                                    }, function() {
                                        //error adding group
                                        MessageService.errorAction();
                                    });
                            return deferred.promise;
                        }
                    }


					function openCloneUserModal(userForm, usernameError) {

                                return $modal
                                        .open({
                                            animation : $scope.animationsEnabled,
                                            templateUrl : 'modules/admin/views/components/security.organizational-hierarchy.create-user.dialog.html',
                                            controller : [
                                                    '$scope',
                                                    '$modalInstance',
                                                    'UtilService',
                                                    function($scope, $modalInstance, Util) {
                                                        $scope.addUser = true;
                                                        $scope.header = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.title";
                                                        $scope.okBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.ok";
                                                        $scope.cancelBtn = "admin.security.organizationalHierarchy.createUserDialog.addLdapMember.btn.cancel";
                                                        $scope.user = userForm;
                                                        $scope.error = usernameError;
                                                        $scope.data = {
                                                            "user" : $scope.user,
                                                            "selectedUser" : selectedUser
                                                        };

                                                        $scope.clearUsernameError = function() {
                                                            if ($scope.error) {
                                                                $scope.error = '';
                                                            }
                                                        };

                                                        $scope.ok = function() {
                                                            $modalInstance.close($scope.data);
                                                        };
                                                    } ],
                                            size : 'sm'
                                        });
                            }

                            function onCloneUser(data, deferred) {
                                LdapUserManagementService.cloneUser(data).then(function(response) {
                                    // add the new user to the list
                                    var element = {};
                                    element.name = response.data.fullName;
                                    element.key = response.data.userId;
                                    element.directory = response.data.userDirectoryName;
                                    $scope.userData.chooseObject.push(element);

                                    //add the new user to cache store
                                    var cacheUsers = new Store.SessionData(LookupService.SessionCacheNames.USERS);
                                    var users = cacheUsers.get();
                                    users.push(element);
                                    cacheUsers.set(users);

                                    MessageService.succsessAction();
                                }, function(error) {
                                    //error adding user
                                    if (error.data.message) {
                                        var usernameError;
                                        var onAdd = function(data) {
                                            return onCloneUser(data);
                                        };
                                        if (error.data.field == 'username') {
                                            usernameError = error.data.message;
                                            openCloneUserModal(error.data.extra.user, usernameError).result.then(onAdd, function() {
                                                deferred.reject("cancel");
                                                return {};
                                            });
                                        } else {
                                            MessageService.error(error.data.message);
                                        }

                                    }

                                    else {
                                        MessageService.errorAction();
                                    }

                                });

                            }

                            function cloneUser() {
                                var modalInstance = openCloneUserModal({}, "");
                                var deferred = $q.defer();
                                modalInstance.result.then(function(data) {
                                    onCloneUser(data, deferred);
                                }, function() {
                                    // Cancel button was clicked
                                });

                            }

                    function fillList(listToFill, data) {
                        _.forEach(data, function(obj) {
                            var element = {};
                            element.name = obj.name;
                            element.key = obj.object_id_s;
                            element.directory = obj.directory_name_s;
                            listToFill.push(element);
                        });
                    }

                    function deleteUser() {
                        LdapUserManagementService.deleteUser(selectedUser).then(function() {

                            var cacheUsers = new Store.SessionData(LookupService.SessionCacheNames.USERS);
                            var users = cacheUsers.get();
                            var cacheKeyUser = _.find(users, {
                                'object_id_s' : selectedUser.key
                            });
                            cacheUsers.remove(cacheKeyUser);

                            $scope.userData.chooseObject = _.reject($scope.userData.chooseObject, function(element) {
                                return element.key === selectedUser.key;
                            });

                            MessageService.succsessAction();
                        }, function() {
                            MessageService.errorAction();
                        });
                    }

                    function userScroll() {
                        initUser($scope.userData.chooseObject.length * 2);
                    }

                    function retrieveDataScroll(data, methodName, panelName) {
                        LdapUserManagementService[methodName](data).then(function(response) {
                            // $scope.userData[panelName] = [];
                            if (_.isArray(response.data)) {
                                $scope.fillList($scope.userData[panelName], response.data);
                            } else {
                                $scope.fillList($scope.userData[panelName], response.data.response.docs);
                            }
                        }, function() {
                            $log.error('Error during calling the method ' + methodName);
                        });
                    }

                    function unauthorizedScroll() {
                        var data = {};
                        data.member_id = lastSelectedUser;
                        data.start = $scope.userData.selectedNotAuthorized.length - 1;
                        data.isAuthorized = false;
                        $scope.retrieveDataScroll(data, "getGroupsForUser", "selectedNotAuthorized");
                    }

                    function authorizedScroll() {
                        var data = {};
                        data.member_id = lastSelectedUser;
                        data.start = $scope.userData.selectedAuthorized.length - 1;
                        data.isAuthorized = true;
                        $scope.retrieveDataScroll(data, "getGroupsForUser", "selectedAuthorized");
                    }

                    function retrieveDataFilter(data, methodName, panelName) {
                        LdapUserManagementService[methodName](data).then(function(response) {
                            $scope.userData[panelName] = [];
                            if (_.isArray(response.data)) {
                                $scope.fillList($scope.userData[panelName], response.data);
                            } else {
                                $scope.fillList($scope.userData[panelName], response.data.response.docs);
                            }
                            if (methodName == "getNUsers" || methodName == "getUsersFiltered") {
                                $scope.selectFirst($scope.userData.chooseObject[0], false);
                            }
                        }, function() {
                            $log.error('Error during calling the method ' + methodName);
                        });
                    }

                    function userManagementFilter(data) {
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 50 : data.n;
                            $scope.retrieveDataFilter(data, "getNUsers", "chooseObject");
                        } else {
                            $scope.retrieveDataFilter(data, "getUsersFiltered", "chooseObject");
                        }
                    }

                    function userUnauthorizedFilter(data) {
                        data.member_id = lastSelectedUser;
                        data.isAuthorized = false;
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 50 : data.n;
                            $scope.retrieveDataFilter(data, "getGroupsForUser", "selectedNotAuthorized");
                        } else {
                            $scope.retrieveDataFilter(data, "getGroupsFiltered", "selectedNotAuthorized");
                        }
                    }

                    function userAuthorizedFilter(data) {
                        data.member_id = lastSelectedUser;
                        data.isAuthorized = true;
                        if (Util.isEmpty(data.filterWord)) {
                            data.n = Util.isEmpty(data.n) ? 50 : data.n;
                            $scope.retrieveDataFilter(data, "getGroupsForUser", "selectedAuthorized");
                        } else {
                            $scope.retrieveDataFilter(data, "getGroupsFiltered", "selectedAuthorized");
                        }
                    }

                } ]);
