'use strict';

angular.module('cases').controller('Cases.DetailsController',
        [ '$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'Case.InfoService', 'MessageService', 'Helper.ObjectBrowserService', function($scope, $stateParams, $translate, Util, ConfigService, CaseInfoService, MessageService, HelperObjectBrowserService) {

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
                componentId: "details",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo
            });

            $scope.options = {
                focus: true,
                dialogsInBody: true
            //,height: 120
            };

            //$scope.editDetails = function() {
            //    $scope.editor.summernote({focus: true});
            //}
            $scope.saveDetails = function() {
                //$scope.editor.destroy();
                var caseInfo = Util.omitNg($scope.objectInfo);
                CaseInfoService.saveCaseInfo(caseInfo).then(function(caseInfo) {
                    MessageService.info($translate.instant("cases.comp.details.informSaved"));
                    return caseInfo;
                });
            };

            //$scope.caseSaveStat = {
            //	dataChanged: false
            //	,dataMoreChanged: false
            //	,saveInitiated: false
            //	,saveInProgress: false
            //	,lastError: null
            //}
            //
            //var saveIt = function() {
            //   console.log('debounce, $scope.caseInfo.details=' + $scope.caseInfo.details);
            //   $scope.caseSaveStat.saveInProgress = true;
            //   $scope.editor.summernote('saveRange');
            //
            //   var caseInfo = Util.omitNg($scope.caseInfo);
            //   CasesService.save({}, caseInfo
            //       ,function(successData) {
            //           console.log('success, successData.details=' + successData.details);
            //           $scope.caseSaveStat.saveInProgress = false;
            //           $scope.editor.summernote('restoreRange');
            //           if ($scope.caseSaveStat.dataMoreChanged) {
            //               console.log('successm dataMoreChanged, $scope.caseInfo.details=' + $scope.caseInfo.details);
            //               initiateSave();
            //
            //           } else {
            //               console.log('successm NOT dataMoreChanged, $scope.caseInfo.details=' + $scope.caseInfo.details);
            //               $scope.caseSaveStat.dataChanged = false;
            //               $scope.caseSaveStat.saveInitiated = false;
            //           }
            //           var z = 1;
            //       }
            //       ,function(errorData) {
            //           console.log('error, $scope.caseInfo.details=' + $scope.caseInfo.details);
            //           $scope.caseSaveStat.lastError = "Failed to save";
            //           $scope.caseSaveStat.saveInProgress = false;
            //           $scope.editor.summernote('restoreRange');
            //           if ($scope.caseSaveStat.dataMoreChanged) {
            //               initiateSave();
            //
            //           } else {
            //               $scope.caseSaveStat.dataChanged = false;
            //               $scope.caseSaveStat.saveInitiated = false;
            //           }
            //           var z = 1;
            //       }
            //   );
            //};
            //var saveLater = _.debounce(saveIt, 1000, false);
            //var initiateSave = function() {
            //   console.log('initiateSave, $scope.caseInfo.details=' + $scope.caseInfo.details);
            //	$scope.caseSaveStat.saveInitiated = true;
            //
            //   $scope.caseSaveStat.dataMoreChanged = false;
            //   saveLater();
            //};
            //$scope.change = function(contents) {
            //	console.log('contents 100: contents=', contents);
            //   return;
            //
            //	if ($scope.caseInfo.details != contents) {
            //       console.log('changed 111: contents=', contents);
            //		$scope.caseInfo.details = contents;
            //		$scope.caseSaveStat.dataChanged = true;
            //		if ($scope.caseSaveStat.saveInProgress) {
            //           console.log('changed 122: contents=', contents);
            //			$scope.caseSaveStat.dataMoreChanged = true;
            //		}
            //       console.log('changed 129: contents=', contents);
            //
            //		//if (true) { //can save
            //			if (!$scope.caseSaveStat.saveInitiated) {
            //               console.log('changed 133: contents=', contents);
            //				initiateSave();
            //               console.log('changed 144: contents=', contents);
            //			}
            //		//}
            //	}
            //   console.log('changed 199: contents=', contents);
            //};

        } ]);