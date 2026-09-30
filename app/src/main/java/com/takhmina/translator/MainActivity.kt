package com.takhmina.translator

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
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
data class Phrase(val en:String,val tg:String,val ru:String,val kk:String,val uz:String,val de:String)

val LANGS = listOf(
    Lang("en","English","🇬🇧"), Lang("tg","Тоҷикӣ","🇹🇯"),
    Lang("ru","Русский","🇷🇺"), Lang("kk","Қазақша","🇰🇿"),
    Lang("uz","O‘zbekcha","🇺🇿"), Lang("de","Deutsch","🇩🇪")
)

val PHRASES = listOf(
    Phrase("Hello","Салом","Привет","Сәлем","Salom","Hallo"),
    Phrase("Good morning","Субҳ ба хайр","Доброе утро","Қайырлы таң","Xayrli tong","Guten Morgen"),
    Phrase("Good evening","Шоми хуш","Добрый вечер","Қайырлы кеш","Xayrli kech","Guten Abend"),
    Phrase("Good night","Шаби хуш","Спокойной ночи","Қайырлы түн","Xayrli tun","Gute Nacht"),
    Phrase("How are you?","Шумо чӣ хелед?","Как вы?","Қалыңыз қалай?","Qalaysiz?","Wie geht es dir?"),
    Phrase("Thank you","Ташаккур","Спасибо","Рақмет","Rahmat","Danke"),
    Phrase("You're welcome","Марҳамат","Пожалуйста","Оқасы жоқ","Arzimaydi","Bitte schön"),
    Phrase("Please","Лутфан","Пожалуйста","Өтінемін","Iltimos","Bitte"),
    Phrase("I love you","Ман туро дӯст медорам","Я тебя люблю","Мен сені жақсы көремін","Men seni sevaman","Ich liebe dich"),
    Phrase("What is your name?","Номи шумо чист?","Как вас зовут?","Атыңыз кім?","Ismingiz nima?","Wie heißt du?"),
    Phrase("My name is Fayzali","Номи ман Файзалӣ аст","Меня зовут Файзали","Менің атым Файзали","Mening ismim Fayzali","Ich heiße Fayzali"),
    Phrase("Where are you from?","Шумо аз куҷоед?","Откуда вы?","Сіз қайдансыз?","Qayerdansiz?","Woher kommst du?"),
    Phrase("I am from Tajikistan","Ман аз Тоҷикистон ҳастам","Я из Таджикистана","Мен Тәжікстаннанмын","Men Tojikistondanman","Ich komme aus Tadschikistan"),
    Phrase("Where is the station?","Истгоҳ дар куҷост?","Где находится вокзал?","Вокзал қайда?","Vokzal qayerda?","Wo ist der Bahnhof?"),
    Phrase("How much does it cost?","Ин чанд пул аст?","Сколько это стоит?","Бұл қанша тұрады?","Bu qancha turadi?","Wie viel kostet das?"),
    Phrase("I don't understand","Ман намефаҳмам","Я не понимаю","Мен түсінбеймін","Men tushunmayapman","Ich verstehe nicht"),
    Phrase("Please speak slowly","Лутфан оҳиста гап занед","Говорите, пожалуйста, медленнее","Баяу сөйлеңізші","Iltimos, sekin gapiring","Bitte sprechen Sie langsam"),
    Phrase("Can you help me?","Метавонед ба ман кӯмак кунед?","Вы можете мне помочь?","Маған көмектесе аласыз ба?","Menga yordam bera olasizmi?","Können Sie mir helfen?"),
    Phrase("I am learning languages","Ман забонҳо меомӯзам","Я изучаю языки","Мен тіл үйреніп жүрмін","Men tillarni o‘rganyapman","Ich lerne Sprachen"),
    Phrase("See you tomorrow","Пагоҳ мебинем","Увидимся завтра","Ертең кездесеміз","Ertaga ko‘rishamiz","Bis morgen")
)

fun value(p:Phrase, code:String)=when(code){
    "en"->p.en;"tg"->p.tg;"ru"->p.ru;"kk"->p.kk;"uz"->p.uz;else->p.de
}

class MainActivity:ComponentActivity(){
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContent{ TakhminaTheme{ TakhminaApp() } }
    }
}

@Composable
fun TakhminaTheme(content:@Composable()->Unit){
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
    var from by remember{mutableStateOf(LANGS[0])}
    var to by remember{mutableStateOf(LANGS[1])}
    Scaffold(
        containerColor=BG,
        bottomBar={
            NavigationBar(containerColor=Color(0xFF0D1222)){
                val nav=listOf(
                    "Translate" to Icons.Default.Translate,
                    "Learn" to Icons.Default.School,
                    "Practice" to Icons.Default.Bolt,
                    "Profile" to Icons.Default.Person
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
                0->TranslatorScreen(from,to,{from=it},{to=it},{xp+=5})
                1->LearnScreen(xp,streak){xp+=10}
                2->PracticeScreen{xp+=15}
                else->ProfileScreen(xp,streak)
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
fun TranslatorScreen(from:Lang,to:Lang,setFrom:(Lang)->Unit,setTo:(Lang)->Unit,onXp:()->Unit){
    val context=LocalContext.current
    val scope=rememberCoroutineScope()
    var input by remember{mutableStateOf(TextFieldValue(""))}
    var output by remember{mutableStateOf("")}
    var loading by remember{mutableStateOf(false)}
    var error by remember{mutableStateOf("")}
    var favorite by remember{mutableStateOf(false)}
    val client=remember{OkHttpClient()}
    val tts=remember{TextToSpeech(context,null)}
    DisposableEffect(Unit){onDispose{tts.shutdown()}}

    fun speak(text:String,lang:Lang){
        val locale=when(lang.code){
            "tg"->Locale("tg","TJ");"ru"->Locale("ru","RU");"kk"->Locale("kk","KZ")
            "uz"->Locale("uz","UZ");"de"->Locale.GERMAN;else->Locale.US
        }
        tts.language=locale
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"takhmina")
    }

    suspend fun online(q:String):String=withContext(Dispatchers.IO){
        val form=FormBody.Builder().add("q",q).add("langpair","${from.code}|${to.code}").build()
        val request=Request.Builder().url("https://api.mymemory.translated.net/get").post(form).build()
        client.newCall(request).execute().use{r->
            if(!r.isSuccessful) throw IllegalStateException("HTTP ${r.code}")
            JSONObject(r.body?.string()?:"").getJSONObject("responseData").getString("translatedText")
        }
    }

    fun translate(){
        if(input.text.isBlank())return
        loading=true;output="";error=""
        scope.launch{
            try{
                output=online(input.text.trim())
                onXp()
            }catch(e:Exception){
                val match=PHRASES.firstOrNull{value(it,from.code).equals(input.text.trim(),true)}
                output=match?.let{value(it,to.code)}?:""
                if(output.isBlank())error="Internet translation is unavailable. Check your connection and try again."
            }finally{loading=false}
        }
    }

    fun swap(){val x=from;setFrom(to);setTo(x);input=TextFieldValue(output.ifBlank{input.text});output=""}

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header("Translator","6 languages • translate and learn in one place")
        Row(Modifier.padding(horizontal=20.dp),verticalAlignment=Alignment.CenterVertically){
            LanguagePicker(from,setFrom)
            IconButton(onClick={swap()}){Icon(Icons.Default.SwapHoriz,"Swap",tint=CYAN)}
            LanguagePicker(to,setTo)
        }
        Spacer(Modifier.height(12.dp))
        Card(Modifier.padding(horizontal=20.dp).fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(17.dp)){
                OutlinedTextField(
                    value=input,onValueChange={input=it;error=""},
                    modifier=Modifier.fillMaxWidth().heightIn(min=145.dp),
                    placeholder={Text("Write, paste or use voice…",color=TEXT2)},
                    label={Text("${from.flag} ${from.name}")},
                    shape=RoundedCornerShape(20.dp)
                )
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.End){
                    IconButton(onClick={}){Icon(Icons.Default.Mic,"Voice input",tint=CYAN)}
                    IconButton(onClick={ {input=TextFieldValue("");output=""} }){Icon(Icons.Default.DeleteOutline,"Clear",tint=TEXT2)}
                }
                Button(
                    onClick={::translate},enabled=input.text.isNotBlank()&&!loading,
                    modifier=Modifier.fillMaxWidth().height(55.dp),shape=RoundedCornerShape(18.dp)
                ){
                    if(loading)CircularProgressIndicator(Modifier.size(22.dp),strokeWidth=2.dp)
                    else{Icon(Icons.Default.AutoAwesome,null);Spacer(Modifier.width(8.dp));Text("TRANSLATE",fontWeight=FontWeight.Bold)}
                }
            }
        }
        if(output.isNotBlank()){
            Spacer(Modifier.height(14.dp))
            Card(Modifier.padding(horizontal=20.dp).fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF202A4A))){
                Column(Modifier.padding(20.dp)){
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Text("${to.flag} ${to.name}",color=CYAN,fontWeight=FontWeight.Bold)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick={ {favorite=!favorite} }){Icon(if(favorite)Icons.Default.Favorite else Icons.Default.FavoriteBorder,"Favorite",tint=if(favorite)Color(0xFFFF5D86) else TEXT2)}
                    }
                    Text(output,fontSize=24.sp,fontWeight=FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row{
                        AssistChip(onClick={},label={Text("+5 XP")},leadingIcon={Icon(Icons.Default.Bolt,null)})
                        Spacer(Modifier.width(7.dp))
                        IconButton(onClick={ {speak(output,to)} }){Icon(Icons.Default.VolumeUp,"Speak")}
                        IconButton(onClick={}){Icon(Icons.Default.ContentCopy,"Copy")}
                    }
                }
            }
        }
        if(error.isNotBlank())Text(error,color=Color(0xFFFF9A9A),Modifier.padding(20.dp))
        Spacer(Modifier.height(20.dp))
        Text("Quick phrases",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        PHRASES.take(8).forEach{p->
            Card(Modifier.padding(horizontal=20.dp,vertical=4.dp).fillMaxWidth().clickable{input=TextFieldValue(value(p,from.code));output=""},RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=Color(0xFF0F1527))){
                Row(Modifier.padding(14.dp),verticalAlignment=Alignment.CenterVertically){
                    Icon(Icons.Default.FlashOn,null,tint=CYAN)
                    Text(value(p,from.code),Modifier.weight(1f).padding(horizontal=12.dp))
                    Icon(Icons.Default.ChevronRight,null,tint=TEXT2)
                }
            }
        }
        Spacer(Modifier.height(25.dp))
    }
}

@Composable
fun LearnScreen(xp:Int,streak:Int,onXp:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header("Learn","A structured path from beginner to confident speaker")
        Card(Modifier.padding(horizontal=20.dp).fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Row(Modifier.padding(20.dp),verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(62.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PRIMARY,CYAN))),contentAlignment=Alignment.Center){Text("🔥",fontSize=27.sp)}
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)){Text("${streak} day streak",fontSize=20.sp,fontWeight=FontWeight.Bold);Text("${xp} XP • Daily goal 20 XP",color=TEXT2)}
                Icon(Icons.Default.ChevronRight,null,tint=CYAN)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("Learning path",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
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
                    Column(Modifier.weight(1f)){Text("${u.first} • ${u.second}",fontWeight=FontWeight.Bold,fontSize=16.sp);Text(u.third,color=TEXT2,fontSize=13.sp)}
                    Icon(if(i==0)Icons.Default.PlayArrow else Icons.Default.Lock,null,tint=if(i==0)CYAN else TEXT2)
                }
            }
        }
        Spacer(Modifier.height(22.dp))
        Text("Skill packs",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
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
fun PracticeScreen(onXp:()->Unit){
    var index by remember{mutableIntStateOf(0)}
    var selected by remember{mutableStateOf("")}
    var checked by remember{mutableStateOf(false)}
    var correctCount by remember{mutableIntStateOf(0)}
    val q=PHRASES[index%PHRASES.size]
    val options=remember(index){listOf(q.tg,PHRASES[(index+3)%PHRASES.size].tg,PHRASES[(index+6)%PHRASES.size].tg).shuffled()}
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header("Practice","Fast challenges • memory • +15 XP")
        Card(Modifier.padding(20.dp).fillMaxWidth(),RoundedCornerShape(28.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(22.dp)){
                Row{Text("Question ${index+1}",color=CYAN,fontWeight=FontWeight.Bold);Spacer(Modifier.weight(1f));Text("${correctCount} correct",color=TEXT2)}
                Spacer(Modifier.height(20.dp))
                Text("Translate into Tajik:",color=TEXT2)
                Text(q.en,fontSize=29.sp,fontWeight=FontWeight.ExtraBold)
                Spacer(Modifier.height(20.dp))
                options.forEach{answer->
                    OutlinedButton(onClick={selected=answer;checked=true},modifier=Modifier.fillMaxWidth().padding(vertical=4.dp),shape=RoundedCornerShape(17.dp),colors=ButtonDefaults.outlinedButtonColors(containerColor=if(selected==answer)PRIMARY.copy(.20f) else Color.Transparent)){Text(answer,Modifier.fillMaxWidth().padding(5.dp))}
                }
                if(checked){
                    val ok=selected==q.tg
                    Text(if(ok)"✓ Excellent! +15 XP" else "Correct answer: ${q.tg}",color=if(ok)GREEN else Color(0xFFFF9C9C),Modifier.padding(top=12.dp))
                    Button(onClick={if(ok){correctCount++;onXp()};index++;selected="";checked=false},Modifier.fillMaxWidth().padding(top=12.dp),shape=RoundedCornerShape(17.dp)){Text("NEXT CHALLENGE")}
                }
            }
        }
        Text("Practice modes",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        listOf("Multiple choice","Word matching","Listening","Fill the gap","Speed round").forEach{ListTile(Icons.Default.Bolt,it,"Daily challenge ready")}
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
fun ProfileScreen(xp:Int,streak:Int){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())){
        Header("Profile","Your progress, achievements and language goals")
        Card(Modifier.padding(20.dp).fillMaxWidth(),RoundedCornerShape(30.dp),colors=CardDefaults.cardColors(containerColor=PANEL)){
            Column(Modifier.padding(22.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(66.dp).clip(CircleShape).background(Brush.linearGradient(listOf(PRIMARY,CYAN))),contentAlignment=Alignment.Center){Text("T",fontSize=30.sp,fontWeight=FontWeight.Black)}
                    Spacer(Modifier.width(14.dp))
                    Column{Text("Takhmina Learner",fontSize=22.sp,fontWeight=FontWeight.ExtraBold);Text("Language explorer",color=TEXT2)}
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){Stat("${xp}","XP");Stat("${streak}","STREAK");Stat("6","LANGUAGES")}
            }
        }
        Text("Achievements",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
        Row(Modifier.padding(20.dp).horizontalScroll(rememberScrollState())){Achievement("🔥","7 day","Keep going");Achievement("⚡","100 XP","First sprint");Achievement("🌍","6 langs","Explorer")}
        Text("Language progress",Modifier.padding(horizontal=20.dp),fontSize=19.sp,fontWeight=FontWeight.Bold)
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
