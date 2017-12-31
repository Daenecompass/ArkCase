'use strict';

angular.module('cases').controller('Cases.ReferencesController', ['$scope', '$stateParams', '$modal'
    , 'UtilService', 'ConfigService', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService'
    , 'ObjectService', 'SearchService', 'Search.QueryBuilderService', 'ObjectAssociation.Service', 'MessageService'
    , function ($scope, $stateParams, $modal
        , Util, ConfigService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService
        , ObjectService, SearchService, SearchQueryBuilder, ObjectAssociationService, MessageService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "references"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var gridHelper = new HelperUiGridService.Grid({scope: $scope});

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            gridHelper.addButton(config, "delete");
            gridHelper.setColumnDefs(config);
            gridHelper.setBasicOptions(config);
            gridHelper.disableGridScrolling(config);
        };

        var onObjectInfoRetrieved = function (objectInfo) {
            $scope.objectInfo = objectInfo;
            $scope.gridOptions = $scope.gridOptions || {};
            refreshGridData(objectInfo.id);
        };

        $scope.onClickObjLink = function (event, rowEntity, targetNameColumnClicked) {
            event.preventDefault();

            var targetType = Util.goodMapValue(rowEntity, "target_type_s");
            var targetId = Util.goodMapValue(rowEntity, "target_id_s");
            var parentId = Util.goodMapValue(rowEntity, "parent_id_s");
            var parentType = Util.goodMapValue(rowEntity, "parent_type_s");
            var fileName = Util.goodMapValue(rowEntity, "target_object.title_parseable");

            if (targetType == ObjectService.ObjectTypes.FILE && targetNameColumnClicked) {
                gridHelper.openObject(targetId, parentId, parentType, fileName);
            } else {
                gridHelper.showObject(targetType, targetId);
            }

            if (ObjectService.ObjectTypes.CASE_FILE == targetType) {
                $scope.$emit('request-show-object', {objectId: targetId, objectType: targetType});
            }
        };

        ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
            $scope.modalConfig = _.find(moduleConfig.components, {id: "referenceSearchGrid"});
            return moduleConfig;
        });

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        // open add reference modal
        $scope.addReference = function () {
            var modalInstance = $modal.open({
                animation: $scope.animationsEnabled,
                templateUrl: 'modules/cases/views/components/case-reference-modal.client.view.html',
                controller: 'Cases.ReferenceModalController',
                size: 'lg',
                resolve: {
                    $filter: function () {
                        var filter = $scope.modalConfig.searchFilter + "&-id:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.CASE_FILE;
                        if ($scope.gridOptions.data.length > 0) {
                            for (var i = 0; i < $scope.gridOptions.data.length; i++) {
                                var data = $scope.gridOptions.data[i];
                                filter += "&-id:" + data.targetId + "-" + data.targetType;
                            }
                        }
                        filter += "&-parent_ref_s:" + $scope.objectInfo.id + "-" + ObjectService.ObjectTypes.CASE_FILE;
                        return filter.replace(/&/gi, '%26');
                    },
                    $config: function () {
                        return $scope.modalConfig;
                    }
                }
            });

            //chosenReference - target
            //parent - objectInfo
            modalInstance.result.then(function (chosenReference) {
                var parent = $scope.objectInfo;
                var target = chosenReference;
                if (target) {
                    var association = ObjectAssociationService.createAssociationInfo(
                        parent.id,
                        ObjectService.ObjectTypes.CASE_FILE,
                        parent.title,
                        parent.caseNumber,
                        target.object_id_s,
                        target.object_type_s,
                        target.title_parseable,
                        target.name,
                        'REFERENCE',
                        'REFERENCE');
                    ObjectAssociationService.saveObjectAssociation(association).then(function (payload) {
                        //success
                        //append new entity as last item in the grid
                        var rowEntity = {
                            object_id_s: payload.associationId,
                            target_object: {
                                name: target.name,
                                title_parseable: target.title_parseable,
                                parent_ref_s: target.parent_ref_s,
                                modified_date_tdt: target.modified_date_tdt,
                                assignee_full_name_lcs: target.assignee_full_name_lcs,
                                object_type_s: target.object_type_s,
                                status_lcs: target.status_lcs
                            },
                            target_type_s: payload.targetType,
                            target_id_s: payload.targetId
                        };

                        $scope.gridOptions.data.push(rowEntity);

                    }, function (errorResponse) {
                        MessageService.error(errorResponse.data);
                    });
                }
            }, function () {
                // Cancel button was clicked.
                return [];
            });

        };

        function refreshGridData(objectId) {
            ObjectAssociationService.getObjectAssociations(objectId, ObjectService.ObjectTypes.CASE_FILE, null).then(function (response) {
                $scope.gridOptions.data = response.response.docs;
            });
        }

        $scope.deleteRow = function (rowEntity) {
            var id = Util.goodMapValue(rowEntity, "object_id_s", 0);
            ObjectAssociationService.deleteAssociationInfo(id).then(function (data) {
                //success
                //remove it from the grid
                _.remove($scope.gridOptions.data, function (row) {
                    return row === rowEntity;
                });
            });
        };
    }
]);