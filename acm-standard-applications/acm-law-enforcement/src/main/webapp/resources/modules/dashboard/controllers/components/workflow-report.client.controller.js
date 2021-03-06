'use strict';

angular.module('dashboard.workflow-report').controller(
        'Dashboard.WorkflowReportController',
        [ '$scope', '$translate', '$state', 'Authentication', 'Dashboard.DashboardService', 'ObjectService', 'ConfigService', 'UtilService', 'Util.DateService', 'params',
                function($scope, $translate, $state, Authentication, DashboardService, ObjectService, ConfigService, Util, UtilDateService, params) {

                    var vm = this;
                    vm.config = null;

                    if (!Util.isEmpty(params.description)) {
                        $scope.$parent.model.description = " - " + params.description;
                    } else {
                        $scope.$parent.model.description = "";
                    }

                    ConfigService.getComponentConfig("dashboard", "workflowReport").then(function(config) {
                        vm.config = config;
                        vm.gridOptions.columnDefs = config.columnDefs;
                        vm.gridOptions.enableFiltering = config.enableFiltering;
                        vm.gridOptions.paginationPageSizes = config.paginationPageSizes;
                        vm.gridOptions.paginationPageSize = config.paginationPageSize;
                        paginationOptions.pageSize = config.paginationPageSize;
                        getPage();
                    });

                    var paginationOptions = {
                        pageNumber: 1,
                        pageSize: 5,
                        sortBy: 'create_date_tdt',
                        sortDir: 'desc'
                    };

                    vm.gridOptions = {
                        enableColumnResizing: true,
                        enableRowSelection: true,
                        enableSelectAll: false,
                        enableRowHeaderSelection: false,
                        useExternalPagination: true,
                        useExternalSorting: true,
                        multiSelect: false,
                        noUnselect: false,
                        columnDefs: [],
                        onRegisterApi: function(gridApi) {
                            vm.gridApi = gridApi;

                            gridApi.core.on.sortChanged($scope, function(grid, sortColumns) {
                                if (sortColumns.length == 0) {
                                    paginationOptions.sort = null;
                                } else {
                                    paginationOptions.sortBy = sortColumns[0].name;
                                    paginationOptions.sortDir = sortColumns[0].sort.direction;
                                }
                                getPage();
                            });
                            gridApi.pagination.on.paginationChanged($scope, function(newPage, pageSize) {
                                paginationOptions.pageNumber = newPage;
                                paginationOptions.pageSize = pageSize;
                                getPage();
                            });
                        }
                    };

                    function getPage() {
                        DashboardService.queryWorkflowReport({
                            sortBy: paginationOptions.sortBy,
                            sortDir: paginationOptions.sortDir,
                            startWith: (paginationOptions.pageNumber - 1) * paginationOptions.pageSize,
                            pageSize: paginationOptions.pageSize
                        }, function(response) {
                            vm.gridOptions.data = [];
                            vm.gridOptions.totalItems = response.numFound;
                            _.forEach(response.data, function(value) {
                                vm.gridOptions.data.push(value);
                            });
                        });
                    }

                    vm.onClickCaseComplaintId = function(objectType, objectId) {
                        ObjectService.showObject(objectType, objectId);
                    };
                } ]);