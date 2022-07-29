# ONEstore InApp Purchase Samples
Sample applications for ONEstore InApp Purchase. 

* [sample_luckyone](https://github.com/ONE-store/onestore_iap_release/tree/master/onestore_iap_sample/sample_luckyone) : Purchase managed and subscription items in your Android app.
* [sample_subscription](https://github.com/ONE-store/onestore_iap_release/tree/master/onestore_iap_sample/sample_subscription) : Purchase subscriptions and manage subscription.

## InApp Purchase SDK
### How to download
Now(IAP API v7), You can download InApp Purchase SDK using the maven system.

Add the maven url to root gradle.

```
repositories {
    ...
    maven { url 'https://repo.onestore.co.kr/repository/onestore-sdk-public' }
}
```

And, add the dependency to your project gradle.

```
dependencies {
    def onestore_iap_version = "21.00.00"
    def onestore_configuration_version = "1.0.0"
    def onestore_configuration_region = "sdk-configuration-kr"
    
    implementation "com.onestorecorp.sdk:sdk-iap:$onestore_iap_version"
    implementation "com.onestorecorp.sdk:$onestore_configuration_region:$onestore_configuration_version"
}
```

If you want to download older SDK(v19), click [This Link](https://github.com/ONE-store/onestore_iap_release/tree/iap19-release/android_app_sample/app/libs)

### Changed the function of the IAP v7(v21) SDK
* Deprecated the Auto product
	* IAP v7 has the Subscription products(week, month, 3 month, 6 month, 1 year)
	* If application has the Auto product of IAP v6, application can still use it.
* Changed the login flow
	* Deprecated the login flow in the IAP SDK and made new class for login flow.
* Changed the deployment flow of json file(global-appstore.json)
	* Can download json file for korea region using the maven.

## Change Note

* 2022-07-29 
	* Uploaded samples for ONEstore Purchase Library v7. 


	