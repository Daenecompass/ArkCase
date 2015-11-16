'use strict';

angular.module('complaints').controller('Complaints.TimeController', ['$scope', '$stateParams', '$q', '$window', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'HelperService', 'LookupService', 'ComplaintsService',
    function ($scope, $stateParams, $q, $window, $translate, Store, Util, Validator, Helper, LookupService, ComplaintsService) {
        var z = 1;
        return;
        $scope.$emit('req-component-config', 'time');
        $scope.$on('component-config', function (e, componentId, config) {
            if (componentId == 'time') {
                Helper.Grid.setColumnDefs($scope, config);
                Helper.Grid.setBasicOptions($scope, config);

                for (var i = 0; i < $scope.config.columnDefs.length; i++) {
                    if ("name" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].cellTemplate = "<a href='#' ng-click='grid.appScope.onClickObjLink($event, row.entity)'>{{row.entity.acm$_formName}}</a>";
                    } else if ("tally" == $scope.config.columnDefs[i].name) {
                        $scope.gridOptions.columnDefs[i].field = "acm$_hours";
                    }
                }
            }
        });


        $scope.$on('complaint-retrieved', function (e, data) {
            if (Validator.validateComplaint(data)) {
                $scope.complaintInfo = data;

                var cacheComplaintTime = new Store.CacheFifo(Helper.CacheNames.CASE_TIME_SHEETS);
                var cacheKey = Helper.ObjectTypes.COMPLAINT + "." + $scope.complaintInfo.id;
                var timesheets = cacheComplaintTime.get(cacheKey);
                Util.serviceCall({
                    service: ComplaintsService.queryTimesheets
                    , param: {
                        objectType: Helper.ObjectTypes.COMPLAINT,
                        objectId: $scope.complaintInfo.id
                    }
                    , result: timesheets
                    , onSuccess: function (data) {
                        if (Validator.validateTimesheets(data)) {
                            timesheets = data;
                            for (var i = 0; i < timesheets.length; i++) {
                                timesheets[i].acm$_formName = $translate.instant("complaints.comp.time.formNamePrefix") + " " + Util.goodValue(timesheets[i].startDate) + " - " + Util.goodValue(timesheets[i].endDate);
                                timesheets[i].acm$_hours = _.reduce(Util.goodArray(timesheets[i].times), function (total, n) {
                                    return total + Util.goodValue(n.value, 0);
                                }, 0);
                            }
                            cacheComplaintTime.put(cacheKey, timesheets);
                            return timesheets;
                        }
                    }
                }).then(
                    function (timesheets) {
                        $scope.gridOptions.data = timesheets;
                        $scope.gridOptions.totalItems = Util.goodValue(timesheets.length, 0);
                        Helper.Grid.hidePagingControlsIfAllDataShown($scope, $scope.gridOptions.totalItems);
                        return timesheets;
                    }
                );
            } //end validate
        });


        $scope.onClickObjLink = function (event, rowEntity) {
            event.preventDefault();
            Helper.Grid.showObject($scope, Helper.ObjectTypes.TIMESHEET, Util.goodMapValue(rowEntity, "id", 0));
        };

    }
]);