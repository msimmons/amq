'use strict';
angular.module('amqApp')
.controller('QueueController', ['$scope', '$routeParams', 'QueueResource',
   function ($scope, $routeParams, QueueResource) {
      $('#queueQuery').focus();
      $scope.currentPage = 1;
      $scope.itemsPerPage = 30;
      $scope.queueData = QueueResource.get({queueName:$routeParams.queueName});

      $scope.messageOnPage = function(index) {
         return (Math.floor(index/$scope.itemsPerPage)+1) == $scope.currentPage;
      }

      $scope.delete = function(message) {
      }

      $scope.refresh = function() {
         $scope.queueData = QueueResource.get({queueName:$routeParams.queueName});
      }
   }
]);