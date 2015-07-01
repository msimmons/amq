angular.module('amqApp').directive('gzDragStart', function() {
   return {
      restrict: 'A',
      scope: {
         handler: '&gzDragStart'
      },
      link: function (scope, element) {
         element.bind('dragstart', function($event) {
            $event.originalEvent.dataTransfer.dropEffect='copy';
            scope.handler();
         });
      }
   }
});

angular.module('amqApp').directive('gzDrop', function() {
   return {
      restrict: 'A',
      scope: {
         dropHandler: '&gzDrop',
         dragHandler: '&gzDragOver'
      },
      link: function (scope, element) {
         element.bind('drop', function($event) {
            $event.preventDefault();
            $event.originalEvent.dataTransfer.dropEffect='copy';
            scope.dropHandler();
         });

         element.bind('dragover', function($event) {
            $event.preventDefault();
            $event.originalEvent.dataTransfer.dropEffect='copy';
            scope.dragHandler();
         });
      }
   }
});
