'use strict';
angular.module('amqApp')
.factory('QueueResource', ['$resource',
function($resource) {
   var resource = $resource('/api/queue/:queueName', {}, {
      query: {method: 'GET', params:{queueName:''}, isArray:true}
   });
   resource.state = {
      selectedQueue: null
   }
   return resource;
}]);