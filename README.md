# ONEstore InApp Purchase Samples
sample applications for ONEstore InApp Purchase. 

* sample_luckyone : Purchase managed and subscription items in your Android app.
* sample_subscription : Purchase subscriptions and manage subscription.

## InApp Purchase SDK
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
    implementation "com.onestorecorp.sdk:sdk-iap:$onestore_iap_version"
}
```

## Change Note

* 2022-07-29 
	* Uploaded samples for ONEstore Purchase Library v7. 


	