'use strict';

var amqApp = angular.module('amqApp', [
      'ngRoute',
      'ngResource',
      'ui.bootstrap',
      'ui.bootstrap.accordion',
      'ui.bootstrap.modal',
]);

amqApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/summary', {
        templateUrl: 'html/summary.html',
        controller: 'SummaryController'
      }).
      when('/queue-list', {
        templateUrl: 'html/queue-list.html',
        controller: 'QueueListController'
      }).
      when('/queue/:queueName', {
        templateUrl: 'html/queue.html',
        controller: 'QueueController'
      }).
      when('/history', {
        templateUrl: 'html/history.html',
        controller: 'HistoryController'
      }).
      otherwise({
        redirectTo: '/summary'
      });
  }]);