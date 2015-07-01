'use strict';
angular.module('amqApp')
.controller('QueueListController', ['$scope', 'QueueResource', '$modal',
   function ($scope, QueueResource, $modal) {
      $('#queueQuery').focus();

      $scope.state = QueueResource.state;

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

      $scope.isSelected = function(queue) {
         return $scope.state.selectedQueue && $scope.state.selectedQueue.name == queue.name;
      }

      $scope.isMessageSelected = function(message) {
         return $scope.currentMessage && $scope.currentMessage.messageId == message.messageId;
      }

      $scope.queueClicked = function(queue) {
         $scope.state.selectedQueue = queue;
         $scope.getData(queue);
      }

      $scope.refreshData = function() {
         if ($scope.state.selectedQueue) $scope.getData($scope.state.selectedQueue);
      }

      $scope.purge = function(queue) {
         QueueResource.delete({queueName:queue.name}).
         $promise.then(
            function(data) {$scope.refresh();},
            function(error) {}
         )
      }

      $scope.deleteMessage = function(message) {
         console.log('Deleting message '+message);
      }

      $scope.refresh = function() {
         $scope.currentMessage = null;
         $scope.queues = QueueResource.query();
         if ( $scope.state.selectedQueue ) {
            $scope.getData($scope.state.selectedQueue);
         }
      }

      $scope.getData = function(queue) {
         $scope.currentMessage = null;
         $scope.queueData = QueueResource.get({queueName:queue.name});
      }

      $scope.messageSelected = function(message) {
         $scope.currentMessage = message;
      }

      $scope.openSend = function(theQueue, theMessage) {
         $scope.sendQueue = theQueue ? theQueue : $scope.state.selectedQueue.name;
         $scope.sendMessage = theMessage;
         var sendMessage = $modal.open({
            animation: true,
            templateUrl: 'sendMessage.html',
            scope: $scope,
            controller: 'QueueListController',
            size: 'lg'
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

      $scope.dragStart = function(message) {
         $scope.draggedMessage = message;
      }

      $scope.dragOver = function(queue) {
      }

      $scope.drop = function(queue) {
         $scope.sendMessage = $scope.draggedMessage.text;
         $scope.openSend(queue.name, $scope.draggedMessage.text);
      }

      $scope.refresh();
   }
]);