# ONEstore InApp Purchase Samples
Sample applications for ONEstore InApp Purchase. 

* [sample_luckyone](https://github.com/ONE-store/onestore_iap_release/tree/master/onestore_iap_sample/sample_luckyone) : Purchase managed and subscription items in your Android app.
* [sample_subscription](https://github.com/ONE-store/onestore_iap_release/tree/master/onestore_iap_sample/sample_subscription) : Purchase subscriptions and manage subscription.

## InApp Purchase SDK
### How to download
Now(IAP API v7), You can download InApp Purchase SDK using the maven system.

Add the maven url to root gradle.

```groovy
repositories {
    maven { url 'https://repo.onestore.net/repository/onestore-sdk-public' }
}
```

And, add the dependency to your project gradle.

```groovy
dependencies {
    def onestore_iap_version = "21.02.01"
    implementation "com.onestorecorp.sdk:sdk-iap:$onestore_iap_version"
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

For more details, please refer to the official guide.
[ONEstore InApp Purchase Guide](https://onestore-dev.gitbook.io/dev/tools/tools/v21/04.-sdk)

## Change Note
* 2025-03-10
    * Fix exception handling bug when using `getApplicationEnabledSetting()`
* 2025-02-25
    * Enhanced developer option features  
    * Added `StoreEnvironment.getStoreType()` API
* 2024-07-01
    * Maven Url changed.
* 2023-12-05
    * In-app v21.01.00 has been applied.
    * `sdk-configuration-xx` is deprecated.  
* 2023-05-18
    * Fixed [issue#5](https://github.com/ONE-store/onestore_iap_release/issues/5)
* 2023-01-09
    * Exception handling when the purchase data is null when calling consume and acknowlege API.
* 2022-07-29 
    * Uploaded samples for ONEstore Purchase Library v7. 


# License
```
Copyright 2023 One store Co., Ltd.

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, 
software distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and
limitations under the License.
```
