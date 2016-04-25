'use strict';

angular.module('cases').controller('Cases.DocumentsController', ['$scope', '$stateParams', '$modal', '$q'
    , 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'DocTreeService'
    , 'Helper.ObjectBrowserService', 'Authentication', 'PermissionsService', 'Object.ModelService'
    , function ($scope, $stateParams, $modal, $q
        , Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, DocTreeService
        , HelperObjectBrowserService, Authentication, PermissionsService, ObjectModelService) {

        Authentication.queryUserInfo().then(
            function (userInfo) {
                $scope.user = userInfo.userId;
                return userInfo;
            }
        );

        var componentHelper = new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "documents"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });


        var onConfigRetrieved = function (config) {
            $scope.treeConfig = config.docTree;
        };

        ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE).then(
            function (formTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(formTypes));
                return formTypes;
            }
        );
        ObjectLookupService.getFileTypes().then(
            function (fileTypes) {
                $scope.fileTypes = $scope.fileTypes || [];
                $scope.fileTypes = $scope.fileTypes.concat(Util.goodArray(fileTypes));
                return fileTypes;
            }
        );


        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
        $scope.objectId = componentHelper.currentObjectId; //$stateParams.id;
        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.objectId = objectInfo.id;
            $scope.assignee = ObjectModelService.getAssignee(objectInfo);
        };


        $scope.uploadForm = function (type, folderId, onCloseForm) {
            return DocTreeService.uploadFrevvoForm(type, folderId, onCloseForm, $scope.objectInfo, $scope.fileTypes);
        };

        $scope.onClickRefresh = function () {
            $scope.treeControl.refreshTree();
        };

        $scope.onAllowCmd = function (cmd, nodes) {
            if (1 == nodes.length) {
                if ("checkin" == cmd) {
                    if (!nodes[0].data.lock) {
                        //there is no lock so checkin should be disabled
                        return "disable";
                    }
                    else if (nodes[0].data.lock
                        && nodes[0].data.lock.creator !== $scope.user) {
                        //there is lock on object but it is not by the user so checkin is disabled
                        return "disable";
                    }
                    else if (nodes[0].data.lock.lockType !== ObjectService.LockTypes.CHECKOUT_LOCK) {
                        //object has lock and it is by the user but it isn't checkout
                        //it is probably edit in word so checkin should be disabled
                        return "disable";
                    }
                    else {
                        //object has lock, lockType is checkout and user is creator
                        return "";

                        //user should be able to unlock the file with checkin if he had permisison
                        //to checkout the file, there is not need to check for permission for unlock

                        /*var allowDeffered = $q.defer();

                         //check permission for unlock
                         PermissionsService.getActionPermission('unlock', nodes[0].data)
                         .then(function success(hasPermission) {
                         if (hasPermission)
                         allowDeffered.resolve("");
                         else
                         allowDeffered.resolve("disable");
                         },
                         function error() {
                         allowDeffered.resolve("disable");
                         }
                         );
                         return allowDeffered.promise;*/
                    }
                }
                else if ("cancelEditing" == cmd) {
                    if (!nodes[0].data.lock) {
                        //there is no lock so cancel is disabled
                        return "disable";
                    }
                    else if (nodes[0].data.lock
                        && (nodes[0].data.lock.creator !== $scope.user
                        || nodes[0].data.lock.creator !== $scope.assignee)) {
                        //object has lock, the user is not creator or owner of parent object

                        //we will check for permissions if it is user that has permission to unlock
                        var allowDeffered = $q.defer();
                        //check permission for unlock
                        PermissionsService.getActionPermission('unlock', nodes[0].data)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        allowDeffered.resolve("");
                                    else
                                        allowDeffered.resolve("disable");
                                },
                                function error() {
                                    allowDeffered.resolve("disable");
                                }
                            );
                        return allowDeffered.promise;
                    }
                    else {
                        //object has lock and user is creator or owner of parent object
                        //so they should be able to unlock
                        return "";
                    }
                }
                else if ("checkout" == cmd || "editWithWord") {
                    if (nodes[0].data.lock) {
                        return "disable";
                    } else {
                        var allowDeffered = $q.defer();
                        //check permission for lock
                        PermissionsService.getActionPermission('lock', nodes[0].data)
                            .then(function success(hasPermission) {
                                    if (hasPermission)
                                        allowDeffered.resolve("");
                                    else
                                        allowDeffered.resolve("disable");

                                },
                                function error() {
                                    allowDeffered.resolve("disable");
                                }
                            );
                        return allowDeffered.promise;
                    }
                }
            }
        };

        $scope.onPreCmd = function (cmd, nodes) {
            //Usage example
            //if ("newFolder" == cmd) {
            //    //custom cmd process
            //    return false; //false indicates don't do default command in core
            //}
            //
            //if ("newFolder" == cmd) {
            //    var df = $q.defer();
            //    $timeout(function() {
            //        //lengthy custom cmd process
            //        df.resolve(true); //true to indicate continue with default command execution
            //    }, 8000);
            //    return df.promise;
            //}
        };

        $scope.onPostCmd = function (cmd, nodes) {
        };
    }
]);
