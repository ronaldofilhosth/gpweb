'use strict';

angular.module('gpApp')
    .directive('logradouro', function(ConsultaCep, $parse) {
        return {
            restrict: 'E',
            templateUrl: 'scripts/components/logradouro/logradouro-layout.html',   
            require: 'ngModel',//informando para o angular que a diretiva usa o controller do ngModel
            link: {
            	
            	post:function ($scope, $element, $attrs, ngModelCtrl){

            		/* quando a página for carregada pela primeira vez para edção de
            		 * cadastro, o endereço será jogado na tela conforme cadastro */ 
            		var ngModelGet = $parse($attrs.ngModel);

                    $scope.$watch($attrs.ngModel, function () {
                        $scope.logradouro = ngModelCtrl.$viewValue;
                    });
				/*Consulta de CEP
				 * Caso no cadastro de endereço existente no banco não conste o CEP informado
				 * o sistema irá procura-lo na internet numa consulta de CEP gratuita,
				 * caso não o encontre, o sistema irá permitir que o cliente entre com
				 * o endereço manualmente*/
            	$scope.findcep = function(valid, cepDom) {
            		$scope.editForm.cep.$valid = true;	
					if (valid){	
						$scope.cep = cepDom;
						if ($scope.cep.length > 7) {
							ConsultaCep.clear;
							/* Consultando endereço na base de dados*/
							ConsultaCep.getLogradouroByCep($scope.cep).then(
									function(data){
										data = angular.fromJson(data);				
										$scope.logradouro = data.data;						
										ngModelCtrl.$setViewValue(data.data);
									},
									//caso o endereço não exista na base ele procura em api gratuita
									function(data){ 
										if (data.status == 404){
											ConsultaCep.getConsultaCepApi($scope.cep).then(
											 function(data){
											  if (!data.erro){
												data = angular.fromJson(data);		
												$scope.logradouro = data;
												ngModelCtrl.$setViewValue(data);
											  }else{											  
												$scope.editForm.cep.$invalid = true;
											  }	
											});
										}
									}
							);
						}
					}
					};	
            }
            }
        
        }
    });