/**
* Handle paginating and sorting of the current list of items using orderBy filter
* and slice.  The config object should have the following:
* {
     predicate: 'the order column',
     reverse: 1 or -1,
     currentPage: the current page,
     itemsPerPage: items displayed per page
* }
*/
angular.module('amqApp').filter('paginate', ['$filter', function($filter) {
    return function(input, config) {
        if ( !input ) return;
        var sorted = $filter('orderBy')(input, config.predicate, config.reverse);
        var start = (config.currentPage-1)*config.itemsPerPage;
        var end = start+config.itemsPerPage;
        return sorted.slice(start, end);
    }
}]);