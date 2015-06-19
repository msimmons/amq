'use strict';
angular.module('amqApp')
.controller('QueueListController', ['$scope', 'QueueResource', '$modal',
   function ($scope, QueueResource, $modal) {
      $('#queueQuery').focus();

      $scope.queuePageConfig = {
         predicate: ['-count', 'name'],
         reverse:0,
         currentPage: 1,
         itemsPerPage: 15
      }

      $scope.messagePageConfig = {
         predicate:'timestamp',
         reverse:-1,
         currentPage: 1,
         itemsPerPage: 10
      };

      $scope.queues = QueueResource.query();

      $scope.isSelected = function(queue) {
         return $scope.selectedQueue && $scope.selectedQueue.name == queue.name;
      }

      $scope.isMessageSelected = function(message) {
         return $scope.currentMessage && $scope.currentMessage.messageId == message.messageId;
      }

      $scope.queueClicked = function(queue) {
         $scope.selectedQueue = queue;
         $scope.getData(queue);
      }

      $scope.purge = function(queue) {
         QueueResource.delete({queueName:queue.name}).
         $promise.then(
            function(data) {$scope.refresh();},
            function(error) {}
         )
      }

      $scope.delete = function(message) {
         console.log('Deleting message '+message);
      }

      $scope.refresh = function() {
         $scope.currentMessage = null;
         $scope.queues = QueueResource.query();
         if ( $scope.selectedQueue ) {
            $scope.getData($scope.selectedQueue);
         }
      }

      $scope.getData = function(queue) {
         $scope.currentMessage = null;
         $scope.queueData = QueueResource.get({queueName:queue.name});
      }

      $scope.messageSelected = function(message) {
         $scope.currentMessage = message;
      }

      $scope.openSend = function() {
         $scope.sendQueue=$scope.selectedQueue ? $scope.selectedQueue.name : '';
         var sendMessage = $modal.open({
            animation: true,
            templateUrl: 'sendMessage.html',
            scope: $scope,
            controller: 'QueueListController',
            size: 'lg',
            resolve: {
               sendQueue: function() {return $scope.sendQueue;}
            }
         });

         sendMessage.result.then(
            function(sendData) {
               QueueResource.save({queueName:sendData.queue}, sendData.message).
                  $promise.then(
                     function(data) {$scope.refresh();},
                     function(error) {}
               );
            },
            function(reason) {
               console.log('modal dismissed '+reason);
            }
         );
      }

      $scope.ok = function() {
         var sendData = {queue:$scope.sendQueue, message:$scope.sendMessage};
         $scope.$close(sendData);
      }

      $scope.cancel = function() {
         $scope.$dismiss('cancelled');
      }

   }
]);