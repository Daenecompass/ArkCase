'use strict';

angular.module('admin').controller('Admin.ReportsScheduleModalController',
        [ '$scope', '$modalInstance', '$q', '$translate', 'LookupService', 'Admin.ScheduleReportService', 'MessageService', 'Util.DateService', 'UtilService', function($scope, $modalInstance, $q, $translate, LookupService, ScheduleReportService, MessageService, UtilDateService, Util) {

            $scope.reportSchedule = {
                reportFile: '',
                reportRecurrence: '',
                reportStartDate: '',
                reportTime: '',
                reportEndDate: '',
                filterStartDate: '',
                filterEndDate: '',
                outputFormat: '',
                reportEmailAddresses: ''

            };
            // instantiate the promise to pull from acm-reports-server.config.properties
            var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");

            // Containers for dropdown/select options
            $scope.reportTypes = [];
            $scope.reportRecurrence = [];
            $scope.outputTypes = [];

            // wait for promises to resolve
            $q.all([ promiseServerConfig ]).then(function(payload) {
                // configure the dropdown/select options
                var allProperties = payload[0];
                // value/label pairs are parsed in angular using format "item.label as item.value for item in {list}"
                $scope.reportTypesList = addProperties($scope.reportTypes, allProperties['REPORT_TYPES']);
                $scope.reportRecurrenceList = addProperties($scope.reportRecurrence, allProperties['REPORT_RECURRENCE']);
                $scope.outputTypesList = addProperties($scope.outputTypes, allProperties['REPORT_OUTPUT_TYPES']);

                // Initialize variables
                if (!Util.isArrayEmpty($scope.reportTypesList)) {
                    $scope.reportFile = $scope.reportTypesList[0].value;
                }
                $scope.reportRecurrence = 'WEEKLY';
                $scope.reportStartDate = new Date();
                $scope.reportEmailAddresses = '';
                $scope.outputFormat = 'Excel/CSV';

                // initialize datepickers
                $scope.opened = {};
                $scope.opened.openedStart = false;
                $scope.opened.openedEnd = false;
                $scope.opened.openedFilterStart = false;
                $scope.opened.openedFilterEnd = false;
            });

            $scope.isSubmitDisabled = function() {
                return Util.isEmpty($scope.reportFile) || Util.isEmpty($scope.reportSchedule.filterStartDate) || Util.isEmpty($scope.reportSchedule.filterEndDate) || Util.isEmpty($scope.reportSchedule.reportStartDate);
            };

            var addProperties = function(propertyArray, propertyString) {
                // value/label pairs are split by commas in the properties files
                var properties = propertyString.split(',');
                for (var i = 0; i < properties.length; i++) {
                    // pairs themselves are split by a dash
                    var singlePropertyValuePair = properties[i].split('-');
                    var valueLabelPair = {
                        value: singlePropertyValuePair[0],
                        label: singlePropertyValuePair[1]
                    };
                    propertyArray.push(valueLabelPair);
                }
                return propertyArray;
            };

            $scope.onClickCancel = function() {
                $modalInstance.dismiss('Cancel');
            };
            $scope.saveNewScheduledReport = function() {

                $modalInstance.close({
                    reportSchedule: $scope.reportSchedule
                });
            };

        } ]);