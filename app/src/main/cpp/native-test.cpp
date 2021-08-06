//
// Created by qaz on 8/5/21.
//
#include <stdio.h>
#include <jni.h>
#include <string>
#include <android/log.h>
#include <stdlib.h>
#include <bitset>
#include <iostream>




//定义TAG之后，我们可以在LogCat通过TAG过滤出NDK打印的日志
#define TAG "Native-Test"
// 定义info信息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
// 定义debug信息
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
// 定义error信息
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)


using namespace std;


class Box{
    //访问修饰符 默认为 private。还可以被定义为 public 或 protected。
    public:
        double length;   // 盒子的长度
        double breadth;  // 盒子的宽度
        double height;   // 盒子的高度
        // 成员函数声明
        Box();//无参构造函数
        Box(double len);//有参构造函数
        ~Box();
        Box(const Box *box);//拷贝构造函数
        double get();
        void set( double len, double bre, double hei );
        void setLength(double length){
            this->length = length;
        }
        double getLength(){
            return this->length;
        }

        friend void printLength(Box &box);

        static int objectCount;
        static int getObjectCount(){
            return objectCount;
        }

};

//静态变量
//声明类的成员为静态时，这意味着无论创建多少个类的对象，静态成员都只有一个副本。
// 初始化类 Box 的静态成员
int Box::objectCount = 10;
//int Box::getObjectCount() {
//    return Box::objectCount;
//}

//内联函数，在编译时，编译器会吧函数的代码副本放在每个调用该函数的地方。
//对内联函数进行任何修改，都需要重新编译函数的所有客户端，因为编译器需要重新更换一次所有的代码，否则将会继续使用旧的函数。
inline int Max(int x, int y)
{
    return (x > y)? x : y;
}


//友元函数，不是任何类的成员函数
//因为 printHeight() 是 Box 的友元，它可以直接访问该类的任何成员,避免吧所有的成员都变为 public ，尽量的保存了数据安全。
void printLength(Box &box){
    LOGE("printHeight %lf ,%lf  %lf",box.length,box.breadth,box.height);
}

// 成员函数定义 无参构造函数
Box::Box() {
    LOGE("Box 无参构造函数 -------  ");
}
// 成员函数定义 有参构造函数
Box::Box(double length) {
    LOGE("Box 有参构造函数 -------  ");
    this->length = length;
}
// 成员函数定义 拷贝构造函数
Box::Box(const Box *box) {
    this->length = box->length;
    LOGE("Box 拷贝构造函数 -------  ");
}
// 成员函数定义 析构函数
Box::~Box() {
    LOGE("Box 析构函数 -------  ");
}
//
// 成员函数定义 析构函数

// 成员函数定义
double Box::get()
{
    return length * breadth * height;
}
// 成员函数定义
void Box::set( double len, double bre, double hei)
{
    length = len;
    breadth = bre;
    height = hei;
}
//c++ 练习
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ndk_jniUtils_Jni_testStruct(JNIEnv *env, jobject thiz) {
//    Box box;// 内部字段有初始化的默认值
//    LOGE("%lf ,%lf  %lf",box.length,box.breadth,box.height);
//    box.set(10,2,9);
//    double d = box.get();
//    LOGE("%lf ",d);
//    box.setLength(100);
//    double length = box.getLength();
//    LOGE("%lf ",length);

//    Box box1(12);
//    printLength(box1);

//    cout << "Max (20,10): " << Max(20,10) << endl;


}

//------------------------------------------------------------------------

//类的继承
//1、公有继承（public）：当一个类派生自公有基类时，基类的公有成员也是派生类的公有成员，基类的保护成员也是派生类的保护成员，
// 基类的私有成员不能直接被派生类访问，但是可以通过调用基类的公有和保护成员来访问。
//2、保护继承（protected）： 当一个类派生自保护基类时，基类的公有和保护成员将成为派生类的保护成员。
//3、私有继承（private）：当一个类派生自私有基类时，基类的公有和保护成员将成为派生类的私有成员。

class BaseA{
    public:
        BaseA(int a, int b);
        ~BaseA();
    public:
        void show();
    protected:
        int m_a;
        int m_b;
};
BaseA::BaseA(int a, int b): m_a(a), m_b(b){
    cout<<"BaseA constructor"<<endl;
}
BaseA::~BaseA(){
    cout<<"BaseA destructor"<<endl;
}
void BaseA::show(){
    cout<<"m_a = "<<m_a<<endl;
    cout<<"m_b = "<<m_b<<endl;
}
//基类
class BaseB{
    public:
        BaseB(int c, int d);
        ~BaseB();
        void show();
    protected:
        int m_c;
        int m_d;
};
BaseB::BaseB(int c, int d): m_c(c), m_d(d){
    cout<<"BaseB constructor"<<endl;
}
BaseB::~BaseB(){
    cout<<"BaseB destructor"<<endl;
}
void BaseB::show(){
    cout<<"m_c = "<<m_c<<endl;
    cout<<"m_d = "<<m_d<<endl;
}
//派生类
class Derived: public BaseA, public BaseB{
    public:
        Derived(int a, int b, int c, int d, int e);
        ~Derived();
    public:
        void display();
    private:
        int m_e;
};
Derived::Derived(int a, int b, int c, int d, int e): BaseA(a, b), BaseB(c, d), m_e(e){
    cout<<"Derived constructor"<<endl;
}
Derived::~Derived(){
    cout<<"Derived destructor"<<endl;
}
void Derived::display(){
    BaseA::show();  //调用BaseA类的show()函数
    BaseB::show();  //调用BaseB类的show()函数
    cout<<"m_e = "<<m_e<<endl;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ndk_jniUtils_Jni_testExtendsClass(JNIEnv *env, jobject thiz) {
    Derived obj(1, 2, 3, 4, 5);
    obj.display();

}