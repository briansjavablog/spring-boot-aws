(function () {
    var springBootAws = angular.module('SpringBootAwsDemo', ['ngRoute', 'angularUtils.directives.dirPagination']);

    springBootAws.directive('active', function ($location) {
        return {
            link: function (scope, element) {
                function makeActiveIfMatchesCurrentPath() {
                    if ($location.path().indexOf(element.find('a').attr('href').substr(1)) > -1) {
                        element.addClass('active');
                    } else {
                        element.removeClass('active');
                    }
                }

                scope.$on('$routeChangeSuccess', function () {
                    makeActiveIfMatchesCurrentPath();
                });
            }
        };
    });
    
    springBootAws.directive('fileModel', [ '$parse', function($parse) {
    	return {
    		restrict : 'A',
    		link : function(scope, element, attrs) {
    			var model = $parse(attrs.fileModel);
    			var modelSetter = model.assign;

    			element.bind('change', function() {
    				scope.$apply(function() {
    					modelSetter(scope, element[0].files[0]);
    				});
    			});
    		}
    	};
    } ]);
    
    springBootAws.controller('CreateCustomerCtrl', function ($scope, $location, $http) {
        var self = this;
        
        self.add = function () {            
        	var customerModel = self.model;        	
        	var savedCustomer;
        	
        	var formData = new FormData();
        	formData.append('firstName', customerModel.firstName);
        	formData.append('lastName', customerModel.lastName);
        	formData.append('dateOfBirth', customerModel.dateOfBirth.getFullYear()  + '-' +  (customerModel.dateOfBirth.getMonth() + 1)  + '-' + customerModel.dateOfBirth.getDay());
        	formData.append('image', customerModel.image);
        	formData.append('street', customerModel.address.street);
        	formData.append('town', customerModel.address.town);
        	formData.append('county', customerModel.address.county);
        	formData.append('postcode', customerModel.address.postcode);
        		
        	$scope.saving=true;
        	$http.post('/spring-boot-aws/customers', formData, {	
        	    transformRequest : angular.identity,
    			headers : {
    				'Content-Type' : undefined
    			}
    		}).success(function(savedCustomer) {
    			$scope.saving=false;
    			$location.path("/view-customer/" + savedCustomer.id);    			
    		}).error(function(data) {
    			$scope.saving=false; 
    		});
        };
    });
    
    springBootAws.controller('ViewCustomerCtrl', function ($scope, $http, $routeParams) {
        
    	var customerId = $routeParams.customerId;    	        
    	$scope.currentPage = 1;
    	$scope.pageSize = 10;
    	
    	$scope.dataLoading = true;
        $http.get('/spring-boot-aws/customers/' + customerId).then(function onSuccess(response) {
        	$scope.customer = response.data;
        	$scope.dataLoading = false;
        }, function onError(response) {
        	$scope.customer = response.statusText;
        	$scope.dataLoading = false;
        });
    });
    
    springBootAws.controller('ViewAllCustomersCtrl', function ($scope, $http) {
    	
    	var self = this;
    	$scope.customers = []; 
    	$scope.searchText;
        
        $scope.dataLoading = true;
        $http.get('/spring-boot-aws/customers').then(function mySucces(response) {
        	$scope.customers = response.data;
        	$scope.dataLoading = false;
        }, function myError(response) {
        	$scope.customer = response.statusText;
        	$scope.dataLoading = false;
        });        
        
        self.delete = function (customerId) {
        	$scope.selectedCustomer = customerId;
        	$scope.customerDelete = true;
        	$http.delete('/spring-boot-aws/customers/' + customerId).then(function onSucces(response) {
            	$scope.customers = _.without($scope.customers, _.findWhere($scope.customers, {id: customerId}));
            	$scope.customerDelete = false;
            }, function onError(){
            	
            });
        },
        
        $scope.searchFilter = function (obj) {
            var re = new RegExp($scope.searchText, 'i');
            return !$scope.searchText || re.test(obj.firstName) || re.test(obj.lastName.toString());
        };
    });
    
    springBootAws.filter('formatDate', function() {
    	return function(input) {
    		return moment(input).format("DD-MM-YYYY");
    	};
    });
    
    springBootAws.config(function ($routeProvider) {
        $routeProvider.when('/home', {templateUrl: 'pages/home.tpl.html'});
        $routeProvider.when('/create-customer', {templateUrl: 'pages/createCustomer.tpl.html'});
        $routeProvider.when('/view-customer/:customerId', {templateUrl: 'pages/viewCustomer.tpl.html'});
        $routeProvider.when('/view-all-customers', {templateUrl: 'pages/viewAllCustomers.tpl.html'});
        $routeProvider.otherwise({redirectTo: '/home'});
    });
    
}());