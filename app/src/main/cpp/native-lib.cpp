#include <jni.h>
#include <string>
#include <android/log.h>
#include <stdlib.h>
#include <ctype.h>
#include <time.h>


//定义TAG之后，我们可以在LogCat通过TAG过滤出NDK打印的日志
#define TAG "JNITEST"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)


extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ndk_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

//字符串的截取
char* subStr1(char *str,int start,int end){
    //开辟一个字符串存储数据，需要计算大小 如：char sub[end-start];
    int len = end - start;
    // malloc 一定要 free
    // 记得加上1 ，在 NDK 一般会采用静态的数组存储 char sub[len]
    char* sub = (char*)malloc(len*sizeof(char)+1);
    // 遍历赋值
    str += start;
    for (int i = 0; i < len; i++){
        sub[i] = *str;
        str++;// 指针往后逻一位
    }
    // 标记字符串结尾，否则 print 无法判断结尾
    sub[len] = '\0';
//    free(sub);
    LOGE("%p\n",sub);
    return sub;
}

// dest 用来存放结果，大小自己指定
// source 需要转换的字符串
void lower(char *dest,char *source){
    while (*source != '\0'){
        // 拿当前字符
        char ch = *source;
        // 转完赋值给 dest
        *dest = tolower(ch);// a -> a A -> a
        // 接着指针 ++ ，遍历下一个
        source++;
        dest++;
    }
    // 标记字符串结尾
    *dest = '\0';
}

void callback(int num){
    LOGE("callback --> %d",num);
}

//函数指针
void operator1(void (*method)(int) ,int num){
    method(num);
}

//动态开辟内存
void dynamicMemory(int num){
    // int arr[num]; 静态开辟，内存大小不能改变
    int* arr = (int*)malloc(sizeof(int)*num);
    int i = 0;
    for (; i < num; i++){
        arr[i] = i;// arr[i] = *(arr+i)
    }
    int new_num = 5;
    int* new_arr = (int*)realloc(arr, sizeof(int)*(num+new_num));
    if (new_arr){// = if(new_arr != NULL)
        for (; i < (num + new_num); i++){
            new_arr[i] = i;// arr[i] = *(arr+i)
        }
    }

    int j = 0;
    for (; j < num + new_num; j++){
        LOGE("新开辟内存的指针：%d ", *new_arr+j);
    }
    if (new_arr){
        // 如果成功了，只要释放新的地址就行
        free(new_arr);
        new_arr = NULL;
    }else{
        // 如果没有成功，释放原来的内存
        free(arr);
    }
}



//native的方法的第二个参数：若是静态方法，对应的是jclass，静态方法不依赖于对应的实例，依赖于类
//非静态方法对应的是jobject 类的对象
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ndk_MainActivity_testStringType(JNIEnv *env, jobject thiz) {//thiz传入的是native所依赖的类的对象，因为是非静态的方法

    LOGE("--------- c start -----------");
    LOGE("int 数据类型所占的字节数：%lu ",sizeof(int));// 4
    LOGE("double 数据类型所占的字节数：%lu  ", sizeof(double));// 8
    LOGE("char 数据类型所占的字节数：%lu  ", sizeof(char));// 1
    LOGE("--------- c end   -----------");
    dynamicMemory(4);
/*    //字符串数组
    char arr[] = {'h','e','l','l','o','-','y','d','l'};
    char *str1 = "yudl3";//这种字符串不能修改
    LOGE("%s",arr);
    int length = sizeof(arr) / sizeof(char);
    int i;
    for(i=0; i<length; i++){
        LOGE("%c  ", *(arr+i) );  //*(arr+i)等价于arr[i]
    }
    //字符串的转换
    char *num_str = "12.55xxx";
    LOGE("%d",atoi(num_str));//转换为int，若是不能转换就是0，后面若是有其他的不是数字的，会被移除 就是 12
    char *num_str1 = "12.5f";
    LOGE("%f",atof(num_str1));//如果不能转换返回的是默认值 0.000000
    char *num_str2 = "12.5xx";
    //把参数 str 所指向的字符串转换为一个浮点数（类型为 double 型）。如果 endptr 不为空，则指向转换中最后一个字符后的字符的指针会存储在 endptr 引用的位置。
    double number = strtod(num_str2, NULL);
    LOGE("%lf",number);
    char str[30] = "20.30300This is test";
    char *ptr;
    double ret = strtod(str, &ptr);
    LOGE("数字（double）是 %lf\n", ret);
    LOGE("字符串部分是 |%s|", ptr);
    //字符串比较
    char* str2 = "Yudl is";
    char* str3 = "yudl";
//    int rc = strncmp(str2, str3, 4);//比较字符串前几个是否相等
    int rc = strcmp(str2, str3);// 区分大小写比较
    if (rc == 0){
        LOGE("相等");
    }else{
        LOGE("不相等");
    }
    //字符串查找
    char *str4 = "name is yudl3";
    char *substr = "yu";
    char *pos = strstr(str4, substr);//返回的是字符串第一次出现的位置（位置指针）, 如果没有找到返回的是空
    int postion = pos - str4;
    if (pos){
        LOGE("%s， %d", "包含",postion);
    }else{
        LOGE("%s", "不包含");
    }
    //字符串的拼接、截取、大小写转换
    // strcpy(); copy进来
    char *str5 = "yudl";
    char *str6 = " is";
    //strlen 计算字符串的长度
    int len = strlen(str5);// cpy[len] android studio 是可以的
    char cpy[20];
    // str 会 copy 到 cpy 里面
    strcpy(cpy, str5);
    LOGE("%s ， %d",cpy,len);
    // 拼接
    strcat(cpy,str6);
    LOGE("%s", cpy);

    //字符串的截取
    char *str7 = "yudlhu is";
    // 截取第三个位置到第五个位置 3，5
    char *sub = subStr1(str7,0,5);
    LOGE("%p\n", sub);
    LOGE("%s",sub);
    // 一定要 free ，因为你的 substr 有动态开辟内存，但是真正开发过程中并不会这么做，自己的方法尽量要自己处理好内存
    free(sub);


    //字符串的大小写转换
    char* name = "aAbBcC";
    char dest[20];
    lower(dest,name);
    LOGE("%s",dest);


    //数组和指针
    int arr10[] = {1,2,3,4,5};
    // for循环在 c 和 C++ 中的正确写法
//    int j = 0;
//    for (; j < 4; j++){
//        LOGE("%d ", arr10[j]);
//    }
    //创建一个指针 指向 数组的首地址
    int *pointer = arr10;
    LOGE("%d",*pointer);
//    pointer++;
    int j = 0;
    for (; j < 4;j++){
        LOGE("位置%d的值是：%d ", j, *(pointer+i));
    }

    //函数指针
    operator1(callback,12);*/

}


