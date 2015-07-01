'use strict';
angular.module('amqApp')
.factory('HistoryResource', ['$resource',
function($resource) {
   var resource = $resource('/api/history/:searchString', {}, {
      query: {method: 'GET', params:{searchString:''}, isArray:true}
   });
   resource.state = {
      searchString:''
   }
   return resource;
}]);