'use strict';
angular.module('amqApp')
.factory('HistoryResource', ['$resource',
function($resource) {
   return $resource('/api/history/:searchString', {}, {
      query: {method: 'GET', params:{searchString:''}, isArray:true}
   });
}]);