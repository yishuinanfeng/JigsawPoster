# JigsawPoster
It is a sample whose performance looks similar to PosterLabs app.

It consisits of a background Image and several pictures which can be moved,scaled,rotated by touch of finger,and corresponding hollow for each picture,the gif below shows how the sample performs:

![image](https://github.com/yanyinan/JigsawPoster/blob/master/%E5%BD%95%E5%B1%8F%E4%B8%93%E5%AE%B6170901115542~4.gif)


we can select one of the picture that can be manipulated,then a menu will show from the bottom of the screen which has three buttons.Two of them in the left can rotate the selected picture 90 degrees and overturn the selected picture respectively,the rightmost can save the whole view as a PNG in the root directory of the phone.All hollows are provided by svg files in the root directory of the phone,and have to be parsed by svg parser which is in the application.

For this is just a sample,so the paths of the files of all the pictures,background images,svgs,destinations of save result of image are hard coding,you had better make it flexibly configured in practice.


Tips：Don't forget to add corresponding svg file in the root of your phone,since the appliction will show a hollow as a common rectangle if it is not able to read corresponding svg file. 











一个效果类似海报工厂的安卓demo。

由一张背景图片，若干张可通过手势进行拖拽、缩放、旋转操作的图片，以及对应的镂空部分组成，可操作的图片只能在镂空部分中显示，效果图如下：

![image](https://github.com/yanyinan/JigsawPoster/blob/master/%E5%BD%95%E5%B1%8F%E4%B8%93%E5%AE%B6170901115542~4.gif)

选中图片，底部弹出操作该图的菜单栏，拥有三个按钮，左边两个可以对该图进行旋转90度和翻转，最右的"保存海报"为将整个视图保存为图片在手机内置sd卡的根路径。
镂空部分需要提供svg文件在手机根路径并通过项目中的svg解析器解析出来。

因为只是一个demo，所以图片、底图、镂空svg文件和保存海报路径均为写死，实际使用应改为可灵活配置。

友情提示：不要忘记添加对应的svg文件到手机sd卡根目录中，因为在读取不到对应的svg文件的情况下只会显示一个矩形区域。
