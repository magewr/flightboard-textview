# flightboard-textview

![](https://github.com/magewr/flightboard-textview/blob/master/sample.gif)

latest version : [![](https://jitpack.io/v/magewr/flightboard-textview.svg)](https://jitpack.io/#magewr/flightboard-textview)



# Usage

Step 1. Add the JitPack repository to your build file

gradle

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.magewr:flightboard-textview:LATEST_VERSION'
	}

Step 3. Sample Code

```
LinearLayout linearLayout = findViewById(R.id.root);

flightBoardTextManager = FlightBoardTextManager.with(this)
	.size(20, 4)
	.textSize(24)
	.blockSize(26)
	.blockMargin(2,0,2,0)
	.textMargin(8,4,8,4)
	.columnAnimationInterval(10)
	.rowAnimationInterval(500)
	.into(linearLayout)
	.skipSameCharacter(true)
	.build();
```
