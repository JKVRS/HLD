Karate FrameWork POC

Karate is BDD(Behaviour-Driven Developmen) testing FrameWork developed by intuit in 2017.

Main Keywords:
url        Service path
path       api path we give ex: '/customerDetails'
request    
response
method
status
Param (set key value)
Header
Cookie
from field


To make use of Karate in a Maven project, we just need to add the karate-junit5 dependency:

 <dependency>
            <groupId>com.intuit.karate</groupId>
            <artifactId>karate-junit5</artifactId>
            <version>1.3.1</version>
            <scope>test</scope>
 </dependency>

 Creating test case : These test cases creating in feature file.

 Feature: Using karate to test customer api

  Background:
    * configure connectTimeout = 80000
    * configure readTimeout = 80000
    * url 'http://localhost:8080/api/v1/customer'
    * def coreCustomerProfileId = '33333333-3333-3333-3333-333333333333'
    * def customerAccountRequest = { customerAccountNumber': '567890123456','customerDob': '1990-07-04',customerDobFormat': 'yyyy-MM-dd' }
    * def customerDebitCardRequest = {"customerDob": "1996/08/10","cardNumber":"3456789034567891","customerDobFormat":"yyyy/MM/dd"}
    * def coreCustomerProfileId = '33333333-3333-3333-3333-333333333333'
    * def updateAddressField = 'update_address'
    * def updatePhoneField = 'update_phone'
    * def updateNameField = 'update_name'
    * def requestBody = {"firstName": "ff","middleName": "d","lastName": "crc","countryCode": "91","telephoneNo": "9090908900000","flatNo": "321","houseNo": "V344","street": "c-11","country": "India","city": "Noi","postalCode": "5658888888888888888888"}


  Scenario: Retrieve customer email and mobile
    Given path '/customerDetails'
    And param coreCustomerProfileId = "33333333-3333-3333-3333-333333333333"
    And param customerDob = "1990-03-03"
    And param customerDobFormat = "yyyy-MM-dd"
    When method get
    Then status 200
    And match $ == read('getCustomerDetails.json')

  Scenario: Update Address
    Given path '/update-profile'
    And param coreCustomerProfileId = coreCustomerProfileId
    And param updateField = updateAddressField
    And request requestBody
    When method put
    Then status 200
    And match response.message == 'Profile updated successfully '

Now we will create .jsosn file where whatever response we are getting we will map that

updateProfile.json
{
    
  "message": "Profile updated successfully ",
  "status": "OK",
  "timestamp": "2024-03-12T21:28:41.6942299"

}
like this we will create json file.

Now to run these test case we will creat a class in test folder form where we can run all the test case

package com.unisys.udb.customer;

import com.intuit.karate.junit5.Karate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")                                                      // active the profile
@TestPropertySource(properties = {"spring.cloud.discovery.enabled=false"})  // this property is in helm chart
@Slf4j
public class CustomerTest {
    @LocalServerPort
    private int port;

    @Karate.Test
    Karate testAll() {
        log.info("Server running on port {}", port);
        return Karate.run().relativeTo(getClass());
    }
}

 