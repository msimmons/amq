'use strict';
angular.module('amqApp')
.controller('HistoryController', ['$scope', 'HistoryResource',
   function ($scope, HistoryResource) {
      $('#searchQuery').focus();

      $scope.historyPageConfig = {
         predicate:'',
         reverse:0,
         itemsPerPage: 25,
         currentPage: 1
      };

      $scope.refresh = function() {
         $scope.history = null;
         $scope.history = HistoryResource.query({searchString:$scope.searchString});
      }

      $scope.formatJson = function(json) {
          var parsedData = JSON.parse(json);
          return JSON.stringify(parsedData, null, 2);
      }

   }
]);