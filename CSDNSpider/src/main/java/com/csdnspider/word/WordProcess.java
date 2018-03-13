package com.csdnspider.word;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import com.chenlb.mmseg4j.ComplexSeg;
import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MMSeg;
import com.chenlb.mmseg4j.Seg;
import com.chenlb.mmseg4j.SimpleSeg;
import com.chenlb.mmseg4j.Word;
import com.chenlb.mmseg4j.example.Simple;

//处理帖子内容
public class WordProcess {

	public static void test(String content) throws IOException {
		File file=new File("E:/搜索平台/词库/");
		Dictionary dic=Dictionary.getInstance(file);
		Seg seg=new ComplexSeg(dic);
		MMSeg mmSeg=new MMSeg(new StringReader(content), seg);
		Word word=null;
		while((word = mmSeg.next())!=null) {
			if(word != null) { 
				System.out.print(word + "|"); 			
			} 
		}		
	}
	
	public static void main(String[] args) throws IOException {
		String content="请问一个关于运算符重载的问题。在重载自加运算符的过程中,同时重载前置自加和后置自加时采用标识参数方法,让编译器识别该两个函数.问题是:编译器是如何认识这两个函数的,在调用过程中并没有传递参数(自己摸索时,尝试将标识参数移动到前置自加函数中,发现注释中的问题)。 编译器对运算符++特殊处理不就行了。 。  我的意思是这两个重载函数是我自己写的,就是想弄明白编译器为什么能区分这两个函数 。  A& operator ++ ();//前置++ A operator ++ (int);//后置++ 。  就是问的这个呀...编译器能区分的原因是什么?您回答的这一点我在题目里已经说过了 。 注意，编译器编译的时候，不是运行的时候 所以 当编译器遇到 你代码 num++ 时 ，则把它转换为相应的函数 num& operator ++ () 同理 但你的代码 ++num 时，编译器把他 operator ++ (int) 。 你把结果和原因弄反了 编译器要区分的是  num++ 和 ++num  不是区分A& operator++() 和 A operator++（int） 。 "
				+ " 您好,请问是怎么区分的呢,为什么编译器就能知道你调用的是哪个.返回值是否为引用应该是不构成重载的,两次调用时也没有传递参数,编译器是如何区分的,还是有什么的方我不知道的(即num++实际上传递了参数(您上面说反了,后置自加才调用传参数的重载函数)),求指正. 。  我前边已经说得很清楚了，你并不是直接 num.operator++ 而是 num++ 对不对? 所以当num++ 时候编译器自动编译为operator++()  就算你直接 opeartor++() 这两个函数的参数也是不一样啊？ 。  您好,我一直问的都是我没有传递参数,编译器却能调用传递了参数的函数.== 。 对，让你说糊涂了前置的没参数，哦、后置是有参数 。 如果，你这么问，那你应该对编译器自动提供，你没有提供构造函数的功能也感到奇怪   。  其实问题可以简化为:为什么num++会调用一个有参数的函数?它向函数里传递了什么? 。 再想深一下，如果你是写一个c++编译器， 当遇到一个自定义类时，程序中对这个自定义类自增运算时，怎么编译？ 。  设置一个参数是书上说这样可以让编译器区分两个函数(如果不设置,就不能构成重载,会报错),然而书上也没说为什么,反正就好使了==! 。 这些是编译器应该做的工作，例如 A b; A a=b; //这里自动调用拷贝构造函数 ============== A b; A a; a=b ;这里自动调用operator== 这些东西谁来区分？ 。  a=b的话就得自己写重载赋值函数,如果不写就会报错 。 一个代码 A a; a++; //或者 ++a; 编译器遇到这样的代码，是不是可以知道 是前置还是后置？ 知道了就调用呗 至于为何调用哪个？两个字“规定” 。  好的,谢谢前辈花费宝贵的时间为我解惑 。  错，不会报错，编译器会自动提供一个opeartor= ,是按位拷贝的 。  嗯嗯,已尝试,是真的== 。 谢谢分享,jijijijijijiji"; 
		WordProcess.test(content);
	}
}
