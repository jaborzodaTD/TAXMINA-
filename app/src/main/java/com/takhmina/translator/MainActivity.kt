package com.takhmina.translator

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.net.URLEncoder
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale

private val BG = Color(0xFF080B16)
private val PANEL = Color(0xFF11172A)
private val PANEL2 = Color(0xFF171F37)
private val PRIMARY = Color(0xFF7C5CFF)
private val CYAN = Color(0xFF35D7FF)
private val GREEN = Color(0xFF54E39B)
private val TEXT2 = Color(0xFFA9B3CC)

data class Lang(val code:String,val name:String,val flag:String)
data class Phrase(val en:String,val tg:String,val ru:String,val kk:String,val uz:String,val de:String,val ka:String)

val LANGS = listOf(
    Lang("tg","Тоҷикӣ","🇹🇯"), Lang("ru","Русский","🇷🇺"),
    Lang("en","English","🇬🇧"), Lang("kk","Қазақша","🇰🇿"),
    Lang("uz","O‘zbekcha","🇺🇿"), Lang("de","Deutsch","🇩🇪"), Lang("ka","ქართული","🇬🇪")
)

val PHRASES = listOf(
    Phrase("Hello","Салом","Привет","Сәлем","Salom","Hallo","გამარჯობა"),
    Phrase("Good morning","Субҳ ба хайр","Доброе утро","Қайырлы таң","Xayrli tong","Guten Morgen","დილა მშვიდობისა"),
    Phrase("Good evening","Шоми хуш","Добрый вечер","Қайырлы кеш","Xayrli kech","Guten Abend","საღამო მშვიდობისა"),
    Phrase("Good night","Шаби хуш","Спокойной ночи","Қайырлы түн","Xayrli tun","Gute Nacht","ღამე მშვიდობისა"),
    Phrase("How are you?","Шумо чӣ хелед?","Как вы?","Қалыңыз қалай?","Qalaysiz?","Wie geht es dir?","როგორ ხარ?"),
    Phrase("Thank you","Ташаккур","Спасибо","Рақмет","Rahmat","Danke","გმადლობთ"),
    Phrase("You're welcome","Марҳамат","Пожалуйста","Оқасы жоқ","Arzimaydi","Bitte schön","არაფრის"),
    Phrase("Please","Лутфан","Пожалуйста","Өтінемін","Iltimos","Bitte","გთხოვთ"),
    Phrase("I love you","Ман туро дӯст медорам","Я тебя люблю","Мен сені жақсы көремін","Men seni sevaman","Ich liebe dich","მე შენ მიყვარხარ"),
    Phrase("What is your name?","Номи шумо чист?","Как вас зовут?","Атыңыз кім?","Ismingiz nima?","Wie heißt du?","რა გქვია?"),
    Phrase("My name is Fayzali","Номи ман Файзалӣ аст","Меня зовут Файзали","Менің атым Файзали","Mening ismim Fayzali","Ich heiße Fayzali","მე მქვია ფაიზალი"),
    Phrase("Where are you from?","Шумо аз куҷоед?","Откуда вы?","Сіз қайдансыз?","Qayerdansiz?","Woher kommst du?","საიდან ხარ?"),
    Phrase("I am from Tajikistan","Ман аз Тоҷикистон ҳастам","Я из Таджикистана","Мен Тәжікстаннанмын","Men Tojikistondanman","Ich komme aus Tadschikistan","მე ტაჯიკეთიდან ვარ"),
    Phrase("Where is the station?","Истгоҳ дар куҷост?","Где находится вокзал?","Вокзал қайда?","Vokzal qayerda?","Wo ist der Bahnhof?","სად არის სადგური?"),
    Phrase("How much does it cost?","Ин чанд пул аст?","Сколько это стоит?","Бұл қанша тұрады?","Bu qancha turadi?","Wie viel kostet das?","რა ღირს?"),
    Phrase("I don't understand","Ман намефаҳмам","Я не понимаю","Мен түсінбеймін","Men tushunmayapman","Ich verstehe nicht","ვერ ვიგებ"),
    Phrase("Please speak slowly","Лутфан оҳиста гап занед","Говорите, пожалуйста, медленнее","Баяу сөйлеңізші","Iltimos, sekin gapiring","Bitte sprechen Sie langsam","გთხოვთ, ნელა ილაპარაკეთ"),
    Phrase("Can you help me?","Метавонед ба ман кӯмак кунед?","Вы можете мне помочь?","Маған көмектесе аласыз ба?","Menga yordam bera olasizmi?","Können Sie mir helfen?","შეგიძლიათ დამეხმაროთ?"),
    Phrase("I am learning languages","Ман забонҳо меомӯзам","Я изучаю языки","Мен тіл үйреніп жүрмін","Men tillarni o‘rganyapman","Ich lerne Sprachen","მე ენებს ვსწავლობ"),
    Phrase("See you tomorrow","Пагоҳ мебинем","Увидимся завтра","Ертең кездесеміз","Ertaga ko‘rishamiz","Bis morgen","ხვალ გნახავ")
)

fun value(p:Phrase, code:String)=when(code){
    "en"->p.en;"tg"->p.tg;"ru"->p.ru;"kk"->p.kk;"uz"->p.uz;"ka"->p.ka;"de"->p.de;else->p.ru
}
fun normalizeText(text:String):String = text.trim().lowercase(Locale.ROOT).replace('ё','е').replace(Regex("\\s+")," ").trimEnd('.','!','?',',','،','؟')

fun offlineTranslate(text:String, from:String, to:String):String? {
    if(from==to) return text.trim()
    val q=normalizeText(text)
    if(from=="ru" && to=="tg") RU_TG_DICTIONARY.firstOrNull{normalizeText(it.ru)==q}?.let{return it.tg}
    if(from=="tg" && to=="ru") RU_TG_DICTIONARY.firstOrNull{normalizeText(it.tg)==q}?.let{return it.ru}
    PHRASES.firstOrNull { normalizeText(value(it,from))==q }?.let { return value(it,to) }
    if(from=="ru" && to=="tg" || from=="tg" && to=="ru"){
        val parts=text.trim().split(Regex("\\s+"))
        var hits=0
        val result=parts.joinToString(" "){token->
            val clean=token.trimEnd('.',',','!','?','،','؟')
            val translated=if(from=="ru") RU_TG_DICTIONARY.firstOrNull{normalizeText(it.ru)==normalizeText(clean)}?.tg
            else RU_TG_DICTIONARY.firstOrNull{normalizeText(it.tg)==normalizeText(clean)}?.ru
            if(translated!=null){hits++; translated}else token
        }
        if(hits>0) return result
    }
    return null
}


class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContent(content = { TakhminaTheme(content = { TakhminaApp() }) })
    }
}

@Composable
fun TakhminaTheme(content: @Composable () -> Unit){
    MaterialTheme(
        colorScheme=darkColorScheme(
            background=BG,surface=PANEL,surfaceVariant=PANEL2,
            primary=PRIMARY,secondary=CYAN,tertiary=GREEN,
            onBackground=Color.White,onSurface=Color.White
        ),content=content
    )
}

@Composable
fun TakhminaApp(){
    var tab by remember{mutableIntStateOf(0)}
    var xp by remember{mutableIntStateOf(120)}
    var streak by remember{mutableIntStateOf(4)}
    var appLang by remember{mutableStateOf("ru")}
    var from by remember{mutableStateOf(LANGS[1])}
    var to by remember{mutableStateOf(LANGS[0])}
    Scaffold(
        containerColor=BG,
        bottomBar={
            NavigationBar(containerColor=Color(0xFF0D1222)){
                val nav=if(appLang=="tg") listOf(
                    "Тарҷума" to Icons.Default.Translate,
                    "Омӯзиш" to Icons.Default.School,
                    "Машқ" to Icons.Default.Bolt,
                    "Профил" to Icons.Default.Person
                ) else listOf(
                    "Перевод" to Icons.Default.Translate,
                    "Обучение" to Icons.Default.School,
                    "Практика" to Icons.Default.Bolt,
                    "Профиль" to Icons.Default.Person
                )
                nav.forEachIndexed{ i,(label,icon)->
                    NavigationBarItem(
                        selected=tab==i,onClick={tab=i},icon={Icon(icon,null)},
                        label={Text(label)},colors=NavigationBarItemDefaults.colors(
                            selectedIconColor=Color.White,selectedTextColor=Color.White,
                            indicatorColor=PRIMARY.copy(alpha=.28f),
                            unselectedIconColor=TEXT2,unselectedTextColor=TEXT2
                        )
                    )
                }
            }
        }
    ){ pad->
        Box(Modifier.fillMaxSize().padding(pad)){
            when(tab){
                0->TranslatorScreen(from,to,{from=it},{to=it},appLang,{xp+=5})
                1->LearnScreen(xp,streak,appLang){xp+=10}
                2->PracticeScreen(appLang){xp+=15}
                else->ProfileScreen(xp,streak,appLang){appLang=it}
            }
        }
    }
}

@Composable
fun Header(title:String,subtitle:String?=null){
    Column(Modifier.padding(start=20.dp,end=20.dp,top=18.dp,bottom=12.dp)){
        Row(verticalAlignment=Alignment.CenterVertically){
            Text("TAXMINA",color=CYAN,fontSize=12.sp,fontWeight=FontWeight.Bold,letterSpacing=2.sp)
            Spacer(Modifier.weight(1f))
            Box(Modifier.size(38.dp).clip(CircleShape).background(
                Brush.linearGradient(listOf(PRIMARY,CYAN))),contentAlignment=Alignment.Center){
                Text("T",fontWeight=FontWeight.Black)
            }
        }
        Spacer(Modifier.height(5.dp))
        Text(title,fontSize=30.sp,fontWeight=FontWeight.ExtraBold)
        subtitle?.let{Text(it,color=TEXT2,fontSize=14.sp)}
    }
}

@Composable
fun LanguagePicker(lang:Lang,onPick:(Lang)->Unit){
    var open by remember{mutableStateOf(false)}
    Box{
        Surface(
            Modifier.clickable{open=true},
            shape=RoundedCornerShape(16.dp),color=PANEL2
        ){Text("${lang.flag}  ${lang.name}",Modifier.padding(horizontal=13.dp,vertical=10.dp),fontWeight=FontWeight.SemiBold)}
        DropdownMenu(open,{open=false}){
            LANGS.forEach{l->DropdownMenuItem(
                text={Text("${l.flag}  ${l.name}")},
                onClick={onClick@{onPick(l);open=false}}
            )}
        }
    }
}

@Composable
fun TranslatorScreen(from:Lang,to:Lang,setFrom:(Lang)->Unit,setTo:(Lang)->Unit,appLang:String,onXp:()->Unit){
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    val ru=appLang!="tg"
    var input by remember{mutableStateOf(TextFieldValue(""))}
    var output by remember{mutableStateOf("")}
    var loading by remember{mutableStateOf(false)}
    var error by remember{mutableStateOf("")}
    var favorite by remember{mutableStateOf(false)}
    val client=remember{OkHttpClient()}
    val voiceLauncher=rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
        val spoken=result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if(!spoken.isNullOrBlank())input=TextFieldValue(spoken)
    }
    val permissionLauncher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){granted->
        if(granted)launchVoice(context,from,voiceLauncher)
    }
    val tts=remember{TextToSpeech(context,null)}
    DisposableEffect(Unit){onDispose{tts.shutdown()}}

    fun speak(text:String){
        val locale=when(to.code){
            "tg"->Locale("tg","TJ");"ru"->Locale("ru","RU");"kk"->Locale("kk","KZ")
            "uz"->Locale("uz","UZ");"de"->Locale.GERMAN;"ka"->Locale("ka","GE");else->Locale.US
        }
        if(tts.setLanguage(locale)<0)tts.language=Locale.US
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"takhmina")
    }

    fun copyText(text:String){
        val clipboard=context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("TAXMINA",text))
        Toast.makeText(context,if(ru)"Скопировано" else "Нусха гирифта шуд",Toast.LENGTH_SHORT).show()
    }

    fun translate(){
        val q=input.text.trim()
        if(q.isBlank())return
        loading=true;output="";error=""
        scope.launch{
            try{
                val local=offlineTranslate(q,from.code,to.code)
                output=local?:onlineTranslate(client,q,from.code,to.code)
                if(output.isBlank())throw IllegalStateException("empty")
                onXp()
            }catch(_:Exception){
                val local=offlineTranslate(q,from.code,to.code)
                if(!local.isNullOrBlank())output=local
                else error=if(ru)"Не удалось перевести. Проверьте интернет и попробуйте ещё раз." else "Тарҷума иҷро нашуд. Интернетро санҷед."
            }finally{loading=false}
        }
    }

    fun swap(){
        val old=from
        setFrom(to);setTo(old)
        input=TextFieldValue(output.ifBlank{input.text})
        output=""
    }

    fun startVoice(){
        if(ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)
            launchVoice(context,from,voiceLauncher)
        else permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header(if(ru)"Переводчик" else "Тарҷумон",if(ru)"Русский ↔ Тоҷикӣ • онлайн + офлайн" else "Тоҷикӣ ↔ Русӣ • онлайн + офлайн")
        Card(Modifier.padding(horizontal=16.dp).fillMaxWidth(),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(16.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    LanguagePicker(from,setFrom)
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick=::swap){Icon(Icons.Default.SwapHoriz,if(ru)"Поменять языки" else "Иваз кардани забонҳо",tint=CYAN)}
                    Spacer(Modifier.width(4.dp))
                    LanguagePicker(to,setTo)
                }
                Spacer(Modifier.height(14.dp))
                Surface(shape=RoundedCornerShape(22.dp),color=Color(0xFF0B1120)){
                    Column(Modifier.padding(14.dp)){
                        Text(from.flag+" "+from.name,color=CYAN,fontWeight=FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value=input,
                            onValueChange={input=it;error=""},
                            modifier=Modifier.fillMaxWidth().heightIn(min=130.dp),
                            placeholder={Text(if(ru)"Напишите здесь: «Привет, как ты?»" else "Ин ҷо нависед: «Салом, чӣ хелӣ?»",color=TEXT2)},
                            textStyle=LocalTextStyle.current.copy(fontSize=20.sp),
                            shape=RoundedCornerShape(18.dp)
                        )
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End){
                            IconButton(onClick=::startVoice){Icon(Icons.Default.Mic,if(ru)"Голос" else "Овоз",tint=CYAN)}
                            IconButton(onClick={input=TextFieldValue("");output="";error=""}){Icon(Icons.Default.DeleteOutline,if(ru)"Очистить" else "Тоза кардан",tint=TEXT2)}
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick=::translate,enabled=input.text.isNotBlank()&&!loading,modifier=Modifier.fillMaxWidth().height(56.dp),shape=RoundedCornerShape(18.dp)){
                    if(loading)CircularProgressIndicator(Modifier.size(22.dp),strokeWidth=2.dp,color=Color.White)
                    else{Icon(Icons.Default.AutoAwesome,null);Spacer(Modifier.width(8.dp));Text(if(ru)"ПЕРЕВЕСТИ" else "ТАРҶУМА КАРДАН",fontWeight=FontWeight.ExtraBold)}
                }
                Spacer(Modifier.height(14.dp))
                Surface(shape=RoundedCornerShape(22.dp),color=PANEL_2){
                    Column(Modifier.fillMaxWidth().padding(16.dp)){
                        Row(verticalAlignment=Alignment.CenterVertically){
                            Text(to.flag+" "+to.name,color=CYAN,fontWeight=FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            if(output.isNotBlank()){
                                IconButton(onClick={favorite=!favorite}){Icon(if(favorite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,if(ru)"Избранное" else "Маъқул",tint=if(favorite)Color(0xFFFF5D86) else TEXT2)}
                                IconButton(onClick={::speak}){Icon(Icons.Default.VolumeUp,if(ru)"Озвучить" else "Бо овоз",tint=CYAN)}
                                IconButton(onClick={ {copyText(output)} }){Icon(Icons.Default.ContentCopy,if(ru)"Копировать" else "Нусха",tint=TEXT2)}
                            }
                        }
                        Spacer(Modifier.height(5.dp))
                        if(output.isBlank())Text(if(ru)"Здесь появится перевод" else "Тарҷума дар ҳамин ҷо пайдо мешавад",color=TEXT2,fontSize=17.sp,modifier=Modifier.padding(vertical=28.dp))
                        else Text(output,fontSize=25.sp,fontWeight=FontWeight.SemiBold,lineHeight=33.sp)
                    }
                }
            }
        }
        if(error.isNotBlank())Text(error,color=Color(0xFFFF9A9A),modifier=Modifier.padding(18.dp))
        Text(if(ru)"Быстрые фразы" else "Ибораҳои зуд",Modifier.padding(horizontal=20.dp,vertical=18.dp),fontSize=20.sp,fontWeight=FontWeight.ExtraBold)
        QUICK_PHRASES.take(8).forEach{p->
            Card(Modifier.padding(horizontal=16.dp,vertical=4.dp).fillMaxWidth().clickable{input=TextFieldValue(value(p,from.code));output=""},RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF0E1527))){
                Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
                    Icon(Icons.Default.FlashOn,null,tint=CYAN)
                    Text(value(p,from.code),Modifier.weight(1f).padding(horizontal=12.dp))
                    Icon(Icons.Default.ChevronRight,null,tint=TEXT2)
                }
            }
        }
        Card(Modifier.padding(16.dp).fillMaxWidth(),RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){
                Icon(Icons.Default.MenuBook,null,tint=CYAN)
                Column(Modifier.weight(1f).padding(horizontal=12.dp)){
                    Text(if(ru)"Офлайн словарь" else "Луғати офлайн",fontWeight=FontWeight.Bold)
                    Text((if(ru)"Русский ↔ Тоҷикӣ • " else "Тоҷикӣ ↔ Русӣ • ")+(RU_TG_DICTIONARY.size*2)+"+ записей",color=TEXT2,fontSize=12.sp)
                }
                Text((RU_TG_DICTIONARY.size*2).toString()+"+",color=GREEN,fontWeight=FontWeight.ExtraBold)
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

private fun launchVoice(context:Context,from:Lang,launcher:androidx.activity.result.ActivityResultLauncher<Intent>){
    val locale=when(from.code){"tg"->"tg-TJ";"ru"->"ru-RU";"kk"->"kk-KZ";"uz"->"uz-UZ";"de"->"de-DE";"ka"->"ka-GE";else->"en-US"}
    val intent=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE,locale)
    }
    try{launcher.launch(intent)}catch(_:Exception){Toast.makeText(context,"Speech recognition unavailable",Toast.LENGTH_SHORT).show()}
}

private suspend fun onlineTranslate(client:OkHttpClient,q:String,from:String,to:String):String=withContext(Dispatchers.IO){
    val encodedQ=URLEncoder.encode(q,"UTF-8")
    val encodedPair=URLEncoder.encode(from+"|"+to,"UTF-8")
    val url="https://api.mymemory.translated.net/get?q="+encodedQ+"&langpair="+encodedPair+"&mt=1"
    val request=Request.Builder().url(url).header("Accept","application/json").get().build()
    client.newCall(request).execute().use{r->
        if(!r.isSuccessful)throw IllegalStateException("HTTP "+r.code)
        val json=JSONObject(r.body?.string()?:"")
        val result=json.optJSONObject("responseData")?.optString("translatedText").orEmpty().trim()
        if(result.isBlank())throw IllegalStateException("empty translation")
        result.replace("&quot;",String.fromCharCode(34)).replace("&#39;",String.fromCharCode(39)).replace("&amp;","&").replace("&lt;","<").replace("&gt;",">")
    }
}

@Composable
fun LearnScreen(xp:Int,streak:Int,appLang:String,onXp:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header(if(appLang=="tg")"Омӯзиш" else "Обучение",if(appLang=="tg")"Аз калимаҳои аввал то гуфтугӯи озод" else "От первых слов до уверенного общения")
        Card(Modifier.padding(horizontal=20.dp).fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(62.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PRIMARY,CYAN))),contentAlignment=Alignment.Center){Text("🔥",fontSize=27.sp)}
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)){Text("${streak} ${if(appLang=="tg")"рӯз пай дар пай" else "дней подряд"}",fontSize=20.sp,fontWeight=FontWeight.Bold);Text("${xp} XP • ${if(appLang=="tg")"Ҳадафи рӯзона 20 XP" else "Дневная цель 20 XP"}",color=TEXT2)}
                Icon(Icons.Default.ChevronRight,null,tint=CYAN)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(if(appLang=="tg")"Роҳи омӯзиш" else "Путь обучения",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        val units=listOf(
            Triple("A1","Greetings & basics","Hello • names • countries"),
            Triple("A1","Everyday life","Food • time • family"),
            Triple("A2","Useful conversations","Questions • directions • shopping"),
            Triple("B1","Work & travel","Jobs • plans • real situations"),
            Triple("B2","Fluency lab","Opinions • natural expressions • nuance")
        )
        units.forEachIndexed{i,u->
            Card(Modifier.padding(horizontal=20.dp,vertical=6.dp).fillMaxWidth().clickable{onXp()},RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=if(i==0)PANEL2 else Color(0xFF0E1425))){
                Row(Modifier.padding(17.dp),verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(48.dp).clip(CircleShape).background(if(i==0)PRIMARY.copy(.28f) else Color(0xFF1A2239)),contentAlignment=Alignment.Center){Text("${i+1}",fontWeight=FontWeight.Bold)}
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)){Text("${u.first} • ${u.second}",fontWeight=FontWeight.Bold,fontSize=16.sp);Text(if(appLang=="tg")"Луғат • грамматика • гуфтор" else u.third,color=TEXT2,fontSize=13.sp)}
                    Icon(if(i==0)Icons.Default.PlayArrow else Icons.Default.Lock,null,tint=if(i==0)CYAN else TEXT2)
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        Text(if(appLang=="tg")"Бахшҳои омӯзишӣ" else "Skill packs",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        Row(Modifier.padding(20.dp).horizontalScroll(rememberScrollState())){listOf("Vocabulary","Grammar","Listening","Speaking","Travel").forEach{SkillCard(it)}}
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun SkillCard(title:String){
    Card(Modifier.width(145.dp).padding(end=10.dp),RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
        Column(Modifier.padding(15.dp)){Icon(Icons.Default.AutoAwesome,null,tint=CYAN);Spacer(Modifier.height(10.dp));Text(title,fontWeight=FontWeight.Bold);Text("10 lessons",color=TEXT2,fontSize=12.sp)}
    }
}

@Composable
fun PracticeScreen(appLang:String,onXp:()->Unit){
    var index by remember{mutableIntStateOf(0)}
    var selected by remember{mutableStateOf("")}
    var checked by remember{mutableStateOf(false)}
    var correctCount by remember{mutableIntStateOf(0)}
    val q=PHRASES[index%PHRASES.size]
    val options=remember(index){listOf(q.tg,PHRASES[(index+3)%PHRASES.size].tg,PHRASES[(index+6)%PHRASES.size].tg).shuffled()}
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header(if(appLang=="tg")"Машқ" else "Практика",if(appLang=="tg")"Саволҳои кӯтоҳ барои мустаҳкам кардани хотира" else "Короткие задания для тренировки памяти")
        Card(Modifier.padding(20.dp).fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(22.dp)){
                Row{Text("${if(appLang=="tg")"Савол" else "Вопрос"} ${index+1}",color=CYAN,fontWeight=FontWeight.Bold);Spacer(Modifier.weight(1f));Text("${correctCount} ${if(appLang=="tg")"дуруст" else "верно"}",color=TEXT2)}
                Spacer(Modifier.height(20.dp))
                Text(if(appLang=="tg")"Ба тоҷикӣ тарҷума кунед:" else "Переведите на таджикский:",color=TEXT2)
                Text(q.en,fontSize=29.sp,fontWeight=FontWeight.ExtraBold)
                Spacer(Modifier.height(20.dp))
                options.forEach{answer->
                    OutlinedButton(onClick={selected=answer;checked=true},modifier=Modifier.fillMaxWidth().padding(vertical=4.dp),shape=RoundedCornerShape(17.dp),colors=ButtonDefaults.outlinedButtonColors(containerColor=if(selected==answer)PRIMARY.copy(.20f) else Color.Transparent)){Text(answer,Modifier.fillMaxWidth().padding(5.dp))}
                }
                if(checked){
                    val ok=selected==q.tg
                    Text(if(ok) "✓ Excellent! +15 XP" else "Correct answer: ${q.tg}", color=if(ok) GREEN else Color(0xFFFF9C9C), modifier=Modifier.padding(top=12.dp))
                    Button(onClick={if(ok){correctCount++;onXp()};index++;selected="";checked=false},Modifier.fillMaxWidth().padding(top=12.dp),shape=RoundedCornerShape(17.dp)){Text(if(appLang=="tg")"САВОЛИ НАВ" else "СЛЕДУЮЩИЙ ВОПРОС")}
                }
            }
        }
        Text(if(appLang=="tg")"Усулҳои машқ" else "Режимы практики",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        listOf(if(appLang=="tg")"Интихоби ҷавоб" else "Выбор ответа",if(appLang=="tg")"Ҷуфт кардани калимаҳо" else "Сопоставление слов",if(appLang=="tg")"Гӯш кардан" else "Аудирование",if(appLang=="tg")"Ҷойи холиро пур кунед" else "Заполнить пропуск",if(appLang=="tg")"Даври тез" else "Быстрый раунд").forEach{ListTile(Icons.Default.Bolt,it,if(appLang=="tg")"Омода" else "Готово")}
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun ListTile(icon:androidx.compose.ui.graphics.vector.ImageVector,title:String,sub:String){
    Row(Modifier.padding(horizontal=20.dp,vertical=5.dp).fillMaxWidth().clip(RoundedCornerShape(17.dp)).background(Color(0xFF0F1527)).padding(15.dp),verticalAlignment=Alignment.CenterVertically){
        Icon(icon,null,tint=CYAN)
        Column(Modifier.weight(1f).padding(horizontal=14.dp)){Text(title,fontWeight=FontWeight.Bold);Text(sub,color=TEXT2,fontSize=12.sp)}
        Icon(Icons.Default.ChevronRight,null,tint=TEXT2)
    }
}

@Composable
fun ProfileScreen(xp:Int,streak:Int,appLang:String,setAppLanguage:(String)->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header(if(appLang=="tg")"Профил" else "Профиль",if(appLang=="tg")"Танзимоти барнома ва пешрафти шумо" else "Настройки приложения и ваш прогресс")
        Card(Modifier.padding(horizontal=20.dp).fillMaxWidth(),RoundedCornerShape(22.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(16.dp)){
                Text(if(appLang=="tg")"Забони барнома" else "Язык приложения",color=TEXT2,fontSize=12.sp)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()){
                    Button(onClick={setAppLanguage("ru")},modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=if(appLang=="ru")PRIMARY else PANEL_2)){Text("🇷🇺 Русский")}
                    Spacer(Modifier.width(8.dp))
                    Button(onClick={setAppLanguage("tg")},modifier=Modifier.weight(1f),colors=ButtonDefaults.buttonColors(containerColor=if(appLang=="tg")PRIMARY else PANEL_2)){Text("🇹🇯 Тоҷикӣ")}
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Card(Modifier.padding(20.dp).fillMaxWidth(),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(22.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(66.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PRIMARY,CYAN))),contentAlignment=Alignment.Center){Text("T",fontSize=30.sp,fontWeight=FontWeight.Black)}
                    Spacer(Modifier.width(14.dp))
                    Column{Text(if(appLang=="tg")"Омӯзандаи TAXMINA" else "TAXMINA Learner",fontSize=22.sp,fontWeight=FontWeight.ExtraBold);Text(if(appLang=="tg")"Ҷаҳонгарди забонҳо" else "Исследователь языков",color=TEXT2)}
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){Stat("${xp}","XP");Stat("${streak}","STREAK");Stat("7","LANGUAGES")}
            }
        }
        Text(if(appLang=="tg")"Натиҷаҳо" else "Достижения",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        Row(Modifier.padding(20.dp).horizontalScroll(rememberScrollState())){Achievement("🔥",if(appLang=="tg")"7 рӯз" else "7 дней",if(appLang=="tg")"Давом диҳед" else "Продолжайте");Achievement("⚡","100 XP",if(appLang=="tg")"Қадами аввал" else "Первый рывок");Achievement("🌍","7 langs",if(appLang=="tg")"Ҷаҳонгард" else "Исследователь")}
        Text(if(appLang=="tg")"Пешрафти забонҳо" else "Прогресс языков",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        LANGS.forEachIndexed{idx,l->
            Column(Modifier.padding(horizontal=20.dp,vertical=7.dp)){
                Row{Text("${l.flag} ${l.name}",Modifier.weight(1f),fontWeight=FontWeight.SemiBold);Text(if(idx==0)"72%" else "28%",color=TEXT2)}
                LinearProgressIndicator(progress={if(idx==0).72f else .28f},modifier=Modifier.fillMaxWidth().padding(top=6.dp),color=if(idx==0)CYAN else PRIMARY.copy(.65f),trackColor=Color(0xFF242D45))
            }
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
fun Stat(value:String,label:String){
    Column(horizontalAlignment=Alignment.CenterHorizontally){Text(value,fontSize=24.sp,fontWeight=FontWeight.ExtraBold,color=CYAN);Text(label,fontSize=10.sp,color=TEXT2)}
}

@Composable
fun Achievement(icon:String,title:String,sub:String){
    Card(Modifier.width(125.dp).padding(end=10.dp),RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
        Column(Modifier.padding(14.dp)){Text(icon,fontSize=28.sp);Text(title,fontWeight=FontWeight.Bold);Text(sub,color=TEXT2,fontSize=11.sp)}
    }
}
