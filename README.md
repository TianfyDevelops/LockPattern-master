# LockPattern

本项目在原作者https://github.com/13971643458/LockPattern-master项目基础上修改而成，仅供apicloud学习交流.  .zip可直接作为自定义模块使用

#1.目录

##lockpattern(apicloud)

## method

openGestureLoginActivity     openCreateGestureActivity

#2.模块概述

lockpattern模块封装了一个通过手势解锁的功能。

#3.方法接口描述

## openGestureLoginActivity

## 打开手势登录界面(用于第一次登陆)

openGestureLoginActivity(callback(ret,err))

## 打开重置手势界面(用于用户修改用户名密码之后调用...)

openCreateGestureActivity(callback(ret,err))

## 打开修改手势界面(用于进入应用修改手势)

openResetGestureActivity(callback(ret,err))

## 打开验证手势界面(用于有手势密码的情况下传递进来checkpassword,直接开启手势解锁界面)

var param={checkpassword:"1234"};

openCheckPwdGestureActivity(param,callback(ret,err))

##返回值说明

{"result:":"CUSTOM"}打开自定义界面

{"result:":"GOTOMAIN"}打开主页面

{"result:":"GOTOLOGIN"}打开登录界面(点击了忘记密码)  

#4.实例代码
```
		
		var uzmoduledemo = null;
		apiready = function(){
	    	uzmoduledemo = api.require('lockpattern');
	    }
	    
		function openGestureLoginActivity(){
		var resultCallback = function(ret, err){
		        document.getElementById("activity_result").innerHTML = JSON.stringify(ret);
			}
	        uzmoduledemo.openGestureLoginActivity(resultCallback);
		}
		
	    function openCreateGestureActivity(){
	    var resultCallback = function(ret, err){
		        document.getElementById("activity_result").innerHTML = JSON.stringify(ret);
			}
	        uzmoduledemo.openCreateGestureActivity(resultCallback);
	    }
	    
	    function openResetGestureActivity(){
		var resultCallback = function(ret, err){
		        document.getElementById("activity_result").innerHTML = JSON.stringify(ret);
			}
	        uzmoduledemo.openResetGestureActivity(resultCallback);
		}
		
	    function openCheckPwdGestureActivity(){
		var resultCallback = function(ret, err){
		        document.getElementById("activity_result").innerHTML = JSON.stringify(ret);
			}
			var param={checkpassword:"1234"};
	        uzmoduledemo.openCheckPwdGestureActivity(param,resultCallback);
		}

```

## Description（java）

Imitate Alipay gesture password

仿支付宝手势密码解锁

## Starting

创建手势密码可以查看 CreateGestureActivity.java 文件.  
登陆验证手势密码可以看 GestureLoginActivity.java 文件.

## Features

* 使用了 JakeWharton/butterknife [butterknife](https://github.com/JakeWharton/butterknife)

* 使用了 ACache 来存储手势密码

```java
/**
 * 保存手势密码
 */
private void saveChosenPattern(List<LockPatternView.Cell> cells) {
    byte[] bytes = LockPatternUtil.patternToHash(cells);
    aCache.put(Constant.GESTURE_PASSWORD, bytes);
}
```

Warning: 使用 ACache 类保存密码并不是无限期的. 具体期限可以查看 ACache 类.

* 使用了 SHA 算法保存手势密码

```java
/**
 * Generate an SHA-1 hash for the pattern. Not the most secure, but it is at
 * least a second level of protection. First level is that the file is in a
 * location only readable by the system process.
 *
 * @param pattern
 * @return the hash of the pattern in a byte array.
 */
public static byte[] patternToHash(List<LockPatternView.Cell> pattern) {
    if (pattern == null) {
        return null;
    } else {
        int size = pattern.size();
        byte[] res = new byte[size];
        for (int i = 0; i < size; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) cell.getIndex();
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            return md.digest(res);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return res;
        }
    }
}
```

* 可以开启震动模式，当选中一个圈的时候，手机会震动

```java
/**
 * Set whether the view will use tactile feedback.  If true, there will be
 * tactile feedback as the user enters the pattern.
 * @param tactileFeedbackEnabled Whether tactile feedback is enabled
 */
public void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
	mEnableHapticFeedback = tactileFeedbackEnabled;
}
```

* 可以开启绘制路径隐藏模式

```java
/**
 * Set whether the view is in stealth mode.  If true, there will be no
 * visible feedback as the user enters the pattern.
 * @param inStealthMode Whether in stealth mode.
 */
public void setInStealthMode(boolean inStealthMode) {
	mInStealthMode = inStealthMode;
}
```


