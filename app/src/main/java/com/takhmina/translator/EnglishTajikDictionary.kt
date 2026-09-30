package com.takhmina.translator

data class EnglishTajikEntry(val en: String, val tg: String)

private val EN_TG_RAW = """
hello|салом
hi|салом
good morning|субҳ ба хайр
good afternoon|рӯз ба хайр
good evening|шоми хуш
good night|шаби хуш
how are you|шумо чӣ хелед
i am fine|ман хубам
nice to meet you|аз шиносоӣ бо шумо хушҳолам
welcome|хуш омадед
see you|то дидор
goodbye|хайр
thank you|ташаккур
thanks|раҳмат
thank you very much|ташаккури зиёд
you are welcome|марҳамат
please|лутфан
excuse me|мебахшед
sorry|бубахшед
no problem|ҳеҷ мушкил нест
of course|албатта
maybe|шояд
yes|ҳа
no|не
okay|хуб
great|олӣ
why|чаро
what|чӣ
who|кӣ
where|куҷо
when|кай
how|чӣ тавр
which|кадом
how much|чанд пул
how many|чанд
i|ман
you|ту
he|ӯ
she|ӯ
we|мо
they|онҳо
this|ин
that|он
here|ин ҷо
there|он ҷо
now|ҳоло
today|имрӯз
tomorrow|пагоҳ
yesterday|дирӯз
morning|субҳ
afternoon|баъд аз нисфирӯзӣ
evening|шом
night|шаб
day|рӯз
week|ҳафта
month|моҳ
year|сол
time|вақт
hour|соат
minute|дақиқа
monday|душанбе
tuesday|сешанбе
wednesday|чоршанбе
thursday|панҷшанбе
friday|ҷумъа
saturday|шанбе
sunday|якшанбе
one|як
two|ду
three|се
four|чор
five|панҷ
six|шаш
seven|ҳафт
eight|ҳашт
nine|нуҳ
ten|даҳ
twenty|бист
thirty|сӣ
forty|чил
fifty|панҷоҳ
one hundred|сад
one thousand|ҳазор
million|миллион
first|якум
second|дуюм
third|сеюм
last|охирин
person|одам
people|мардум
man|мард
woman|зан
boy|писар
girl|духтар
child|кӯдак
friend|дӯст
family|оила
mother|модар
father|падар
brother|бародар
sister|хоҳар
son|писар
daughter|духтар
husband|шавҳар
wife|ҳамсар
parents|волидон
neighbor|ҳамсоя
boss|роҳбар
worker|коргар
customer|муштарӣ
teacher|муаллим
student|донишҷӯ
doctor|духтур
driver|ронанда
home|хона
house|хона
apartment|манзил
room|ҳуҷра
door|дар
window|тиреза
wall|девор
floor|фарш
kitchen|ошхона
bathroom|ҳаммом
bedroom|ҳуҷраи хоб
bed|кат
table|миз
chair|курсӣ
key|калид
bag|сумка
clothes|либос
shirt|курта
shoes|пойафзол
phone|телефон
charger|пуркунандаи барқ
battery|батарея
computer|компютер
food|хӯрок
water|об
bread|нон
rice|биринҷ
meat|гӯшт
chicken|мурғ
fish|моҳӣ
milk|шир
tea|чой
coffee|қаҳва
sugar|шакар
salt|намак
egg|тухм
apple|себ
banana|банан
orange|афлесун
potato|картошка
onion|пиёз
tomato|помидор
breakfast|наҳорӣ
lunch|хӯроки нисфирӯзӣ
dinner|шомона
restaurant|тарабхона
menu|меню
bill|ҳисоб
hungry|гурусна
thirsty|ташна
shop|мағоза
store|мағоза
market|бозор
price|нарх
money|пул
cash|пули нақд
card|корт
salary|маош
cheap|арзон
expensive|қимат
discount|тахфиф
buy|харидан
sell|фурӯхтан
pay|пардохт кардан
change|бақия
receipt|расид
work|кор
job|ҷойи кор
office|идора
company|ширкат
business|тиҷорат
meeting|вохӯрӣ
schedule|ҷадвал
break|танаффус
holiday|таътил
contract|шартнома
document|ҳуҷҷат
passport|шиноснома
address|суроға
application|ариза
signature|имзо
go|рафтан
come|омадан
arrive|расидан
leave|рафтан
return|баргаштан
stay|мондан
wait|интизор шудан
help|кӯмак кардан
call|занг задан
answer|ҷавоб додан
ask|пурсидан
tell|гуфтан
say|гуфтан
speak|сухан гуфтан
listen|гӯш кардан
hear|шунидан
see|дидан
look|нигоҳ кардан
watch|тамошо кардан
read|хондан
write|навиштан
learn|омӯхтан
study|таҳсил кардан
work hard|сахт кор кардан
understand|фаҳмидан
know|донистан
remember|дар хотир доштан
forget|фаромӯш кардан
think|фикр кардан
want|хостан
need|зарур доштан
like|писандидан
love|дӯст доштан
can|тавонистан
cannot|наметавонам
must|бояд
should|бояд
try|кӯшиш кардан
start|оғоз кардан
finish|тамом кардан
open|кушодан
close|бастан
find|ёфтан
search|ҷустуҷӯ кардан
send|фиристодан
receive|гирифтан
download|зеркашӣ кардан
upload|боргузорӣ кардан
share|мубодила кардан
save|захира кардан
delete|нест кардан
check|санҷидан
choose|интихоб кардан
use|истифода кардан
make|сохтан
do|кардан
give|додан
take|гирифтан
bring|овардан
put|гузоштан
keep|нигоҳ доштан
big|калон
small|хурд
long|дароз
short|кӯтоҳ
new|нав
old|кӯҳна
good|хуб
bad|бад
beautiful|зебо
easy|осон
difficult|душвор
fast|тез
slow|оҳиста
hot|гарм
cold|сард
important|муҳим
necessary|зарур
ready|омода
busy|машғул
free|озод
right|дуруст
wrong|нодуруст
safe|бехатар
dangerous|хатарнок
strong|қавӣ
weak|заиф
young|ҷавон
happy|хушбахт
sad|ғамгин
tired|хаста
sick|бемор
healthy|солим
afraid|тарсида
angry|хашмгин
bus|автобус
train|қатора
plane|ҳавопаймо
taxi|таксӣ
car|мошин
station|истгоҳ
airport|фурудгоҳ
road|роҳ
street|кӯча
city|шаҳр
village|деҳа
country|кишвар
hotel|меҳмонхона
ticket|чипта
trip|сафар
travel|сафар кардан
map|харита
left|чап
right side|тарафи рост
straight|рост
near|наздик
far|дур
head|сар
eye|чашм
ear|гӯш
nose|бинӣ
mouth|даҳон
hand|даст
foot|пой
heart|дил
body|бадан
health|саломатӣ
pain|дард
fever|таб
medicine|дору
hospital|беморхона
pharmacy|дорухона
emergency|ҳолати фавқулода
school|мактаб
university|донишгоҳ
lesson|дарс
book|китоб
notebook|дафтар
pen|қалам
exam|имтиҳон
knowledge|дониш
language|забон
word|калима
sentence|ҷумла
question|савол
answer|ҷавоб
translation|тарҷума
translator|тарҷумон
dictionary|луғат
text|матн
letter|ҳарф
number|рақам
internet|интернет
website|сомона
app|барнома
file|файл
code|код
password|рамз
account|ҳисоб
message|паём
photo|сурат
video|видео
music|мусиқӣ
link|пайванд
online|онлайн
offline|офлайн
connection|пайвастшавӣ
network|шабака
settings|танзимот
copy|нусха гирифтан
clear|тоза кардан
translate|тарҷума кардан
sun|офтоб
rain|борон
snow|барф
wind|шамол
cloud|абр
sky|осмон
earth|замин
sea|баҳр
mountain|кӯҳ
river|дарё
weather|обу ҳаво
i am hungry|ман гуруснаам
i am thirsty|ман ташнаам
where is the bathroom|ҳаммом дар куҷост
where is the toilet|ҳоҷатхона дар куҷост
how much is this|ин чанд пул аст
i don't understand|ман намефаҳмам
speak slowly|оҳиста гап занед
speak in tajik|ба тоҷикӣ гап занед
speak in english|ба англисӣ гап занед
can you help me|метавонед ба ман кӯмак кунед
what does this mean|ин чӣ маъно дорад
write it down|инро нависед
repeat please|лутфан такрор кунед
wait a moment|як лаҳза интизор шавед
i don't know|ман намедонам
i know|ман медонам
i understand|ман мефаҳмам
i need help|ба ман кӯмак лозим аст
i am lost|ман роҳро гум кардам
what time is it|соат чанд аст
what is your name|номи шумо чист
my name is|номи ман ... аст
where are you from|шумо аз куҷоед
i am from tajikistan|ман аз Тоҷикистон ҳастам
i live in kazakhstan|ман дар Қазоқистон зиндагӣ мекунам
i speak tajik|ман ба тоҷикӣ гап мезанам
i speak a little english|ман каме англисӣ медонам
i am learning english|ман англисӣ меомӯзам
please help|лутфан кӯмак кунед
call an ambulance|ёрии таъҷилӣ даъват кунед
call the police|полисро даъват кунед
be careful|эҳтиёт бошед
good luck|барори кор
congratulations|табрик
happy birthday|зодрӯз муборак
i miss you|ман туро пазмон шудаам
i love you|ман туро дӯст медорам
take care|худатонро эҳтиёт кунед
have a nice day|рӯзи хуб дошта бошед
see you soon|ба зудӣ мебинем
""".trimIndent().lines()

val EN_TG_DICTIONARY: List<EnglishTajikEntry> = EN_TG_RAW.mapNotNull {
    val parts = it.split("|", limit = 2)
    if (parts.size == 2) EnglishTajikEntry(parts[0], parts[1]) else null
}
