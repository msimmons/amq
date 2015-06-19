'use strict';
angular.module('amqApp')
.factory('QueueResource', ['$resource',
function($resource) {
   return $resource('/api/queue/:queueName', {}, {
      query: {method: 'GET', params:{queueName:''}, isArray:true}
   });
}]);