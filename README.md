# 基于Springboot房屋租赁系统(协同推荐算法)
## 一、系统介绍
包括管理员、房东、租客三种角色，外加游客(未登录情况) 出租类型包含整租和合租
权限 游客 < 租客 < 房东 < 管理员
## 算法推荐
基于用户行为，收藏，浏览时长，下订单等行为，做算法推荐
### 1、游客功能
登录、注册(可以注册房东或租客)、搜索房子、查看箱子信息、新闻咨询查询
搜索功能，根据价格范围，面积范围，城市，小区，整租/合租搜索
房子信息，基本信息、轮播图、地图定位
### 2、租客功能
预定房子(创建订单、签订合同、支付订单)
订单管理(取消订单、查看订单、查看合同、退租)
我的家(查看我的当前有效订单房子信息、查看合租情况)
反馈管理(提交反馈、反馈列表、删除反馈)
收藏管理(收藏房子、收藏列表、取消收藏)
个人信息修改、密码修改、联系房东(自动给房东发邮件)
### 3、房东功能
房子管理(发布出租/编辑房子信息，发布后需要管理员审核；删除房子；下架房子)
订单管理(订单列表、退租、查看合同)
发布出租(包括标题、描述、价格、各种配套信息、地图位置、轮播图等20多个字段信息)
反馈管理(提交反馈、反馈列表、删除反馈)
个人信息、密码修改
### 4、管理员功能
用户管理(禁用用户、启用用户)
房子管理(编辑房子、审核通过/驳回房子、下架房子)
订单管理(订单列表、退租、查看合同)
反馈管理(反馈列表、删除反馈、处理反馈)
新闻管理(新闻列表、发布新闻)

## 二、效果展示
### 用户端
![contents](picture/picture1.png)
![contents](picture/picture2.png)
![contents](picture/picture3.png)
![contents](picture/picture4.png)
![contents](picture/picture5.png)
![contents](picture/picture6.png)
![contents](picture/picture7.png)
![contents](picture/picture8.png)
![contents](picture/picture9.png)
![contents](picture/picture10.png)
![contents](picture/picture11.png)
![contents](picture/picture12.png)
![contents](picture/picture13.png)
![contents](picture/picture14.png)
![contents](picture/picture15.png)

### 管理员端
![contents](picture/picture16.png)
![contents](picture/picture17.png)
![contents](picture/picture18.png)
![contents](picture/picture19.png)
![contents](picture/picture20.png)
![contents](picture/picture21.png)
![contents](picture/picture22.png)

## 三、所用技术
后端技术栈：
springboot+mybatis+mysql+shiro
前端技术栈：
jsp
bootstrap
vue

## 四、环境介绍
基础环境 :IDEA/eclipse, JDK 1.8, Mysql5.7及以上,Mave
源码+数据库脚本
所有项目以及源代码本人均调试运行无问题 可支持远程调试运行
