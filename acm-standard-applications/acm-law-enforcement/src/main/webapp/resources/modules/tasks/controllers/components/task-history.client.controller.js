'use strict';

angular.module('tasks').controller('Tasks.HistoryController', ['$scope', '$stateParams', '$q'
    , 'UtilService', 'ConfigService', 'Helper.UiGridService', 'ObjectService', 'Object.AuditService'
    , 'Task.InfoService', 'Helper.ObjectBrowserService', 'Acm.StoreService'
    , function ($scope, $stateParams, $q
        , Util, ConfigService, HelperUiGridService, ObjectService, ObjectAuditService, TaskInfoService
        , HelperObjectBrowserService, Store) {

        var componentHelper = new HelperObjectBrowserService.Component({
            moduleId: "tasks"
            , componentId: "history"
            , scope: $scope
            , stateParams: $stateParams
            , retrieveObjectInfo: TaskInfoService.getTaskInfo
            , validateObjectInfo: TaskInfoService.validateTaskInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});
        var promiseUsers = gridHelper.getUsers();

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
            gridHelper.setExternalPaging(config, $scope.gretrieveGridData);
            gridHelper.setUserNameFilter(promiseUsers);

            $scope.retrieveGridData();
        };
        
        var subscribeForUpdate = function() {
            if ($scope.subscription) {
              $scope.$bus.unsubscribe($scope.subscription);
            }
            var eventName = "object.changed/" + ObjectService.ObjectTypes.TASK + "/" + $stateParams.id;
            $scope.subscription = $scope.$bus.subscribe(eventName, function(data) {
                // invalidate cache here
                var cacheKey = ObjectService.ObjectTypes.TASK + '.' + $stateParams.id + '.0.10..asc';
                new Store.CacheFifo(ObjectAuditService.CacheNames.AUDIT_DATA).remove(cacheKey);
                $scope.retrieveGridData();
            });
        };
      
        subscribeForUpdate();

        $scope.retrieveGridData = function () {
            if (Util.goodPositive(componentHelper.currentObjectId, false)) {
                var promiseQueryAudit = ObjectAuditService.queryAudit(ObjectService.ObjectTypes.TASK
                    , componentHelper.currentObjectId
                    , Util.goodValue($scope.start, 0)
                    , Util.goodValue($scope.pageSize, 10)
                    , Util.goodMapValue($scope.sort, "by")
                    , Util.goodMapValue($scope.sort, "dir")
                );

                $q.all([promiseQueryAudit]).then(function (data) {
                    var auditData = data[0];
                    $scope.gridOptions.data = auditData.resultPage;
                    $scope.gridOptions.totalItems = auditData.totalCount;
                    //gridHelper.hidePagingControlsIfAllDataShown($scope.gridOptions.totalItems);
                });
            }
        };
    }
]);