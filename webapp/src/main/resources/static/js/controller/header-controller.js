'use strict';
angular.module('amqApp')
.controller('HeaderController', ['$scope', '$location',
   function ($scope, $location) {

      $scope.isActive = function(viewLocation) {
         return $location.path().match(viewLocation);
      }

   }
]);