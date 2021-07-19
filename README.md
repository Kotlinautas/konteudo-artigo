> Este repositório é referente ao artigo [Criando uma API com Ktor](https://dev.to/kotlinautas/criando-uma-api-com-ktor-8le), este repositório se destina unicamente a armazenar a aplicação final.

## Kotlinautas

Esse conteúdo é oferecido e distribuído pela comunidade [Kotlinautas](https://twitter.com/kotlinautas/), uma comunidade brasileira que busca oferecer conteúdo gratuito sobre a linguagem Kotlin em um espaço plural.

![capa Kotlinautas](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/jreawpvk6whxigcpmctf.png)

## Introdução

### O quê é Ktor?

Ktor é um framework para Kotlin para criar microserviços, _websockets_, MVC's e serviços web em geral.

Ktor tem uma diferença em relação aos frameworks que geralmente são usados com a linguagem Kotlin. Ktor é feito inteiramente em Kotlin, diferente de outros frameworks, como Spring ou Micronaut. Essa diferença faz com que o Ktor se encaixe perfeitamente com Kotlin, enquanto que os outros frameworks que não são feitos inteiramente em Kotlin (Pois foram feitos inicialmente para Java) não conseguem fazer.

Ktor tem um [site oficial](https://ktor.io/) onde pode ser encontrada a sua documentação.

### O quê iremos construir?

Vamos construir uma API simples, que possibilite a criação de usuários e de artigos, e cada artigo será pertencente á um único usuário. A função deste exercício é apenas aprender como uma aplicação funciona no Ktor, como podemos criar rotas, como funciona um _model_, como conectar á um banco de dados, etc.

Caso você sinta dificuldade em alguma parte, como a estrutura de arquivos, onde um certo arquivo está, etc. você pode checar todos os arquivos [neste repositório do GitHub](https://github.com/kotlinautas/konteudo-artigo).

### Materiais

Para este artigo, recomendo que você utilize a [IDE IntelliJ](https://www.jetbrains.com/idea/). tanto a versão gratuita _Community_, quanto a paga _Ultimate_ poderão ser usadas neste artigo. Pois com ela, processos como instalar as dependências do Gradle, executar a aplicação, escrever o código, etc. se tornarão bem mais fáceis.

Também é necessário ter o [Kotlin](https://kotlinlang.org/) instalado na máquina, e um conhecimento mínimo da linguagem.

### Criando um projeto

O [site oficial](https://ktor.io/) oferece um [método para criarmos um projeto pronto do Ktor](https://start.ktor.io/#), isso significa que podemos escolher diversas features, bibliotecas, configurações, etc. para já virem no projeto. Isso facilita a criação de um projeto, pois podemos apenas baixar o arquivo ZIP deste projeto pronto, e descomprimi-lo para começarmos a codar.

![Site start.ktor.io](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/vj6umrg2trzvzv33wm8t.png)

  Dentro do site [start.ktor.io](https://start.ktor.io), deixe as opções do menu assim, com o **website** sendo `konteudo`, o artifact sendo `konteudo.konteudo`, e a engine como Netty.

![Configurações recomendadas para o projeto](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/094xcxnl63jcfadg0um6.png)

Na parte de plugins, adicione `contentNegociation`, `kotlinx.serialization`, `GSON` e `Routing`:

![plugins adicionados](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/j427wn5ajh2jrtvl5cip.png)

  Após isso, clique no botão **Generate Project** para baixar o arquivo ZIP com o projeto.

### Carregando o projeto no IntelliJ

  Provavelmente, após você abrir o seu IntelliJ, ele deve estar parecido com isto:

![IntelliJ em sua tela inicial](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/bou1vr522jkizp69q5lu.png)

Essa é a tela inicial do IntelliJ, para abrir um projeto, clique no botão **Open** acima, e selecione a pasta que está o projeto que baixamos anteriormente (Já descompactado)

  Com isso, o IntelliJ carregará os arquivos junto da interface.

  É normal o IntelliJ automaticamente instalar as dependências e fazer a *build* de um novo projeto, então, aguarde um pouco antes de começar a mexer na interface.

  Após isso, será como usar um editor de código qualquer, clicar duas vezes em um arquivo para abrir, botão direito na aba com os arquivos para criar um novo arquivo/classe, etc.

## Iniciando os trabalhos

  Observe que já algumas pastas e arquivos que não criamos, mas sim vieram por padrão no arquivo ZIP. Há algumas pastas, mas podemos dar destaque á 3, `resources`, `src` e `test`.

  - `resources` é uma pasta que se refere á algumas configurações do Ktor. Não iremos utiliza-lá por enquanto.

  - `src` é a pasta onde estará os nossos arquivos de código, essa será a pasta que mais iremos utilizar

  - `test` é a pasta que iremos armazenar os testes da nossa aplicação, neste artigo, não iremos usar esta pasta.

  Abra o arquivo `src/main/kotlin/konteudo/Application.kt`, este arquivo é onde a nossa aplicação está. Vamos destrinchar algumas partes deste arquivo.

### Imports

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import konteudo.plugins.*
```

Essas linhas iniciais, importam diversas funcionalidades do Ktor, que iremos usar ao longo da aplicação.

### Main

```kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
```

Essa linha representa a função principal da nossa aplicação, a função `main`. Que está apenas iniciando o servidor [Netty](https://netty.io/), que é o servidor que escolhemos para rodar a nossa aplicação. Com isso não vamos precisar nos preocupar com configuração de Apache, Nginx, etc. Além disso, essa função inicia outras 2, sendo `configureSerialization` e `configureRouting`. Que cada uma está em um arquivo separado na pasta `plugins`

### Serialization

No arquivo `src/main/kotlin/konteudo/plugins/Serialization.kt` há a função `configureSerialization`:

```kotlin
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
            gson {
        }
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
```

Neste trecho, instalamos um _Content Negotiation_, que servirá para recebermos e enviarmos JSON de uma maneira bem mais simples, tranformando objetos JSON em objetos do Kotlin. e também utilizamos o **GSON** para fazer o processamento de JSON da nossa aplicação.

Além disso são criadas duas rotas, ambas para desmonstração do JSON da nossa aplicação. Mas vamos ver sobre rotas um pouco mais á frente

### Roteamento

No arquivo `src/main/kotlin/konteudo/plugins/Routing.kt` há a função `configureRouting`, que é:

```kotlin
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

Neste trecho, é mostrado a maneira que as rotas são criadas, uma das melhores maneiras de fazer isso é usando métodos `get()`, `post()`, `delete()`, `update()`, etc. que representam os métodos HTTP.

No Ktor, para responder apenas com texto, podemos usar a função `call.respondText()`, informando o texto.

Com isso em mente, por padrão é criado uma rota `/`. A rota `/` apenas é um _Hello World_. Mas vamos relembrar que há outras duas rotas para JSON, sendo `/json/kotlinx-serialization`, e `/json/gson`. As duas são iguais, e estão apenas para mostrar que a nossa aplicação consegue receber e enviar JSON.

Tente utilizar alguma aplicação que consiga enviar requisições para testar essas três rotas. Como o [Insomnia](https://insomnia.rest/) que é um cliente HTTP gráfico para desktop, ou um cliente HTTP via linha de comando caso você já tenha experiência.

### Executando a aplicação

Observe que ao lado da função main, há uma seta. Essa seta pode ser clicada e a aplicação será executada.

![seta ao lado da função main](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/vc6ryjo7jdz0mau7dzvx.png)

E com a aplicação em execução, uma aba se abrirá abaixo, mostrando todos os logs da aplicação.

![aplicação em execução](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/aao53l9imkd7uacyuk3d.png)

Além dessa maneira, também é possível apertar o botão direito no arquivo `Application.kt` e depois em **Run**, e também abrir um terminal, e executar `./gradlew :run`.

### Instalando dependências: Exposed

[Exposed](https://github.com/JetBrains/Exposed) é uma biblioteca de ORM para Kotlin, isso significa que ela nos ajudará a conectar á um banco de dados, e inserir, remover, ler, e alterar dandos de dentro dele. Funcionando para:

- H2
- MariaDB
- MySQL
- Oracle
- PostgreSQL
- SQL Server
- SQLite

Para instalar, usando o Gradle Kotlin, primeiro, abra o arquivo `build.gradle.kts`, e no topo, veja que há 3 variáveis:

```kotlin
val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
```

Essas 3 variáveis são variáveis de ambiente, isso significa que estão definidas em um outro lugar, e neste arquivo apenas o seu valor está sendo pego. Mas, aonde foram definidas? simples, no arquivo `gradle.properties`.

Abra este arquivo, e você irá achar algo parecido com isso:

```
logback_version=1.2.1
ktor_version=1.5.4
kotlin.code.style=official
kotlin_version=1.4.32
```

Nestas linhas, algumas versões estão definidas, dessa maneira, podemos ter um controle central das versões das dependências.

Com esse conceito em mente, adicione a linha:

```
exposedVersion=0.31.1
```

Ao arquivo `gradle.properties`, assim, teremos a versão do exposed fixa.

Volte ao arquivo `build.gradle.kts`, e adicione na secção das variáveis de ambiente, a variável `exposedVersion`

```kotlin
val logback_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
+   val exposedVersion: String by project
```

e mais para o final do arquivo, na parte de `dependencies`, adicione três dependências á mais, que são referentes ao Exposed.

```kotlin
dependencies {
    implementation("io.ktor:ktor-serialization:$ktor_version")
        implementation("io.ktor:ktor-server-core:$ktor_version")
        implementation("io.ktor:ktor-gson:$ktor_version")
        implementation("io.ktor:ktor-server-netty:$ktor_version")
        implementation("ch.qos.logback:logback-classic:$logback_version")
        testImplementation("io.ktor:ktor-server-tests:$ktor_version")
        testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")

+           implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
+           implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
+           implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
}

```

Após isso, para instalarmos essas dependências, aperte Ctrl+Shift+O ou clique no botão **Load Gradle Changes**, no lado superior direito

![botão Load Gradle Changes](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/jakvpzb4agwfk1dmhm67.png)

Com isso, as dependências serão instaladas adequadamente.

## Criando Models: User

Agora vamos partir para algo mais prático, vamos criar duas coisas:

- Uma classe que represente um usuário para o Ktor
- Uma tabela que represente um usuário para um banco de dados

Com essa classe e tabela, iremos conseguir representar um usuário, recebendo e enviando, e também fazendo mudanças nessa tabela no banco de dados.

Por enquanto, como nossa aplicação é apenas um exercício, vamos dizer que este usuário, apenas terá um nome, sem senha e outros dados. Mas em uma aplicação real, será necessário estes outros dados.

Primeiro, crie um pacote na pasta `src/main/kotlin/konteudo`, esse pacote é uma pasta que irá armazenar os models da aplicação, e inicialmente, apenas o model **User**. Aperte o botão direito na pasta `src/main/kotlin/konteudo`, **New** e depois em **Package**.

Após isso, crie um arquivo Kotlin dentro dessa pasta, com o nome `User.kt`, para fazer isso, clique com o botão direito, **New**, e depois em **Kotlin Class/File**.

Neste arquivo, vamos importar inicialmente uma Annotation (anotação), que a `@Seriazable`, que irá transformar objetos Kotlin em JSON, e JSON em objetos Kotlin. Para importa-lá, adicione esse import:


```kotlin
import kotlinx.serialization.Serializable
```

Após isso, adicione uma classe que represente um usuário, onde apenas terá um ID, para ser um identificador único, e também um nome.

```kotlin
@Serializable
data class User (var id: String? = null, val name: String)
```

- `@Serializable` deixa explícito que a classe abaixo poderá ser transformada em JSON e JSON poderá ser transformado nessa classe
- `data class` é usado para classes que apenas irão guardar dados, como é o caso dessa.
- `id` está sendo criado com `var` pois é um campo mutável, podendo assim mudar seu valor, e no seu final, há um `String? = null`, isso é uma maneira de fazer que esse campo também seja opicional para o `kotlinx.serialization`, com seu valor padrão sendo `null` caso não seja preenchido.

Agora já temos a classe, agora vamos criar a tabela que represente os usuários. Mas para isso, não iremos precisar escrever o SQL na mão, apenas precisamos escrever uma tabela usando o Exposed, e usar o próprio Exposed para criar essa tabela automaticamente para nós.

Primeiro, importe o exposed no arquivo `src/main/kotlin/kotlin/models/User.kt`:

```kotlin
import org.jetbrains.exposed.sql.*
```

```kotlin
object Users: Table(){
    val id: Column<String> = char("id", 36)
    val name: Column<String> = varchar("name", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_Users_Id")

    fun toUser(row: ResultRow): User = User(
         id = row[Users.id],
         name = row[Users.name],
     )
}
```

- É criado um objeto chamado User, que será uma Tabela (`Table()`)
- São criadas duas variáveis, sendo `id` e `name`. As duas são do tipo `Column<String>` que representa uma coluna em um banco de dados, uma sendo `char` de 36 caracteres (UUID), e a outra sendo um `varchar` com tamanho máximo de 50.
- É definido a chave primária sendo o ID, determinado que um ID só pode pertencer á um único usuário.
- Ao final, é criada uma função chamada `toUser()` que iremos usar para transformar uma linha da tabela, em um usuário. Entenderemos melhor porque essa função é definida mais á frente.

### Model Final

Com isso, o arquivo completo estará assim:

```kotlin
package konteudo.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*

object Users: Table(){
    val id: Column<String> = char("id", 36)
    val name: Column<String> = varchar("name", 50)
    override val primaryKey = PrimaryKey(id, name = "PK_Users_Id")

    fun toUser(row: ResultRow): User = User(
        id = row[Users.id],
        name = row[Users.name],
    )
}

@Serializable
data class User (var id: String? = null, val name: String)
```

## Conectando á um banco de dados

Agora vamos conectar á um banco de dados, no caso, iremos usar um banco de dados em memória, que será o **H2**, um banco de dados em Java.

Para instalar o H2, faça o mesmo processo, mas com essa dependência:

```kotlin
implementation("com.h2database:h2:1.4.199")
```

Agora, vamos criar um arquivo em `src/main/kotlin/konteudo/` chamado `initDB.kt`, esse arquivo terá uma função que irá conectar ao banco de dados. e realizar uma migration, que irá transformar a classe `User` em uma tabela.

Primeiro, vamos importar algumas coisas que vamos usar:

```kotlin
import konteudo.models.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
```

Após isso, vamos criar uma variável chamada `initDB()`:

```kotlin
fun initDB(){

}

```

Dentro dessa função, primeiramente vamos usar a função `Database.connect` para nos conectarmos ao banco **H2**.

```kotlin
fun initDB(){
    Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
}
```

E ao final, vamos inserir um bloco `transaction{ }` que representa uma transação ao banco, e dentro deste, vamos rodar a migration usando a função `SchemaUtils.create()` passando a tabela `Users` como argumento:

```kotlin
fun initDB(){
    Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
    }
}
```

Agora vamos ao arquivo `src/main/kotlin/konteudo/Application.kt` e vamos adicionar a função `initDB()` para fazer a conexão na inicialização da aplicação:

```kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
+   initDB()

    configureSerialization()
    configureRouting()
    }.start(wait = true)
}
```

Pronto, agora estamos conetados á um banco de dados, e já podemos utiliza-ló.

## Criando Rotas: User

Agora vamos criar as rotas que serão usadas para criar, ler, e remover usuários. Essas rotas vão permitir que um eventual frontend, possa fazer alterações no banco de dados usando o backend para isso.

Crie um novo pacote chamado `routes/` em `src/main/kotlin/konteudo`, e um arquivo dentro de `routes/` chamado `UserRoutes.kt`.

Já vamos começar esse arquivo fazendo diversas importações de recursos que vamos usar:

```kotlin
import konteudo.models.User
import konteudo.models.Users
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID;
```

Agora vamos criar uma função para armazenar as rotas, que pode se chamar por exemplo, `UserRouting()`:

```kotlin
fun Route.userRouting(){
}
```

dentro dessa função, teremos que inserir a rota que será usada, que no caso, vamos chamar de `/user`

```kotlin
fun Route.userRouting(){
    route("/user"){

    }
}
```

agora, dentro de `route("/user")` poderemos inserir as funções `get()`, `post()`, `delete()`, `put()`, etc. para começar, vamos iniciar um com um GET.

## GET /user

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {

        }
    }
}
```

Pronto, agora pode ser enviado um GET para a rota `/user`. Esse GET irá servir para pegar todos os usuários, e enviar como uma lista como resposta da requisição.

Para pegar essa lista, será necessário usar o Exposed para se comunicar com o banco e pegar essa lista, após isso, teremos que pegar todas as linhas do banco de dados que foram selecionadas, e transformar em instâncias da classe `User` usando a função `toUser()`. Com isso em mente, o nosso código ficará assim:

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            val users = transaction {
            Users.selectAll().map { Users.toUser(it) }
            }
        }
    }
}
```

- No trecho acima, é necessário usar um bloco chamado `transaction` para receber a transação ao banco de dados.
- Dentro desse bloco, selecionamos todas as linhas da tabela `users`, usando o método `Users.selectAll()` e transformamos cada linha em uma instância da classe `User`.
- E uma lista com todos esses dados, é definida na variável `users`.

Pronto! Agora temos uma lista com todos esses dados, apenas precisamos enviar esses dados como resposta da requisição.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            val users = transaction {
                Users.selectAll().map { Users.toUser(it) }
            }

            return@get call.respond(users)
        }
    }
}
```

Para fechar essa primeira rota, vamos usar uma anotação chamada `return@get` que deixa explícito que é o retorno de uma requisição GET.

Para fazer essa resposta, podemos usar a função `call.respond()`, que irá responder a requisição, passando a lista `users` como objeto.

Essa lista apenas está podendo ser usada como resposta, pois adicionamos a anotação `@Serializable` á classe `User` e porque temos o **GSON** para fazer a resposta e envio de JSON.

### GET /user/id

Agora vamos criar algo um pouco diferente, vamos criar uma rota que possibilite buscar por um usuário em específico usando o seu ID, com a rota sendo `/user/{id do usuário}`.

Primeiro, precisamos criar uma outra rota GET, que permita que enviamos um ID ao final da URL, isso pode ser feito dessa maneira.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){

        }
    }
}
```

Agora vamos pegar o ID que foi enviado e guardar em uma variável.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                "User not found", status = HttpStatusCode.NotFound
            )
        }
    }
}
```

- É definida uma variável chamada `id` que recebe o valor do parâmetro `id` da requisição, usando `call.parameters["id"]`, e usando o operador ternário simplificado `?:`, caso esse ID não seja informado na requisição será enviado um erro 404
- Esse erro 404 será enviado usando um `return@get` para deixar claro que será o retorno da função, e como um 404 pode ser apenas uma resposta em texto, foi usado um `call.respondText`, que necessita de uma mensagem, e um status, que representa o status HTTP da resposta, que no caso é 404.

Após isso, precisamos pegar o usuário com esse ID específico. Podemos fazer isso dessa maneira:

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                "User not found", status = HttpStatusCode.NotFound
            )

            val user: List<User> = transaction { Users.select { Users.id eq id }.map { Users.toUser(it) }}
        }
    }
}
```

- Criamos uma variável chamada `user`, essa variável vai receber uma lista de instâncias da classe `user`, essa lista poderá armazenar apenas um usuário, pois um ID pertence á só um usuário.
- Para fazer a transação, é usado o bloco `transaction`, e para selecionar linhas de uma tabela usando um critério específico, podemos usar o método `Users.select {}` informando o critério, que no caso é `Users.id eq id`, onde necessariamente o ID da linha deve ser igual á variável `id`. Após isso, todas as linhas (uma no caso) são tranformadas em instâncias da classe `User` com a função `toUser`.

Agora precisamos responder a requisição, retornando esse usuário em específico.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                "User not found", status = HttpStatusCode.NotFound
            )

            val user: List<User> = transaction { Users.select { Users.id eq id }.map { Users.toUser(it) }}

            if (user.isNotEmpty()) {
                return@get call.respond(user.first())
            }
            return@get call.respondText("User not found", status = HttpStatusCode.NotFound)
        }
    }
}
```

- Agora, após a definição da variável `user`, caso essa lista seja vazia (nenhum usuário com certo ID específico) será enviado como resposta um 404, com a mensagem `User not found` (usuário não encontrado).
- Mas, caso essa lista tenha itens dentro, será enviado como resposta o primeiro usuário, usando `call.respond` para responder com o valor de uma variável, passando `user.first()` que pega o primeiro usuário.

### POST /user

Agora já temos duas rotas para pegar usuários, mas também precisamos criar uma rota que possibilite a criação de novos usuários, no caso, a rota POST.

Primeiro, vamos criar a rota POST:

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{

        }
    }
}
```

Agora vamos usar um recurso do **GSON** e do `@Seriazable` que adicionamos da classe `User`, vamos transformar o corpo da requisição, em uma instância da classe `User`, dessa maneira:

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            val user = call.receive<User>()
        }
    }
}
```

Mas há um dado que não pode ser definido no corpo da requisição, que é o ID desse usuário. Para isso, vamos usar a biblioteca de `UUID` para gerar esse ID único.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            val user = call.receive<User>()

            user.id = UUID.randomUUID().toString()
        }
    }
}
```

- Usamos o método `UUID.randomUUID()` para gerar um UUID aleatório, e convertemos esse UUID para String usando o `.toString()` ao final.
- Após isso, guardamos esse UUID dentro de `user.id`, para assim, esse usuário ter um ID definido.

Pronto, agora temos essa instância da classe `User` pronta para ser inserida no banco de dados.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            val user = call.receive<User>()

            user.id = UUID.randomUUID().toString()

            transaction {
                Users.insert {
                    it[id] = user.id!!
                    it[name] = user.name
                }
            }

            call.respondText("Created", status = HttpStatusCode.Created)
        }
    }
}
```

- Agora usamos um outro bloco `transaction` para inserir mais uma linha á tabela `users`, usando o método `Users.insert {}` e definindo o valor de cada coluna, como por exemplo `it[id] = user.id`.
- Na linha `it[id] = article.id!!` foram usados dois sinais de exclamação `!!` é para definir o valor de `it[id]` garantindo que o valor de `article.id` não seja nulo. Isso é para garantir que esse valor tenha mudado, pois esse valor inicia-se como nulo.
- Após isso, a requisição é respondida, com um status 201, que representa que algo foi criado no banco de dados.

Pronto! Agora poderemos inserir novos usuários.

### DELETE /user/id

Agora vamos criar uma rota para deletar usuários, sendo essa a última rota que vamos criar agora.

primeiro, vamos criar a rota DELETE.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            ...
        }

        delete("id"){

        }
    }
}
```

Agora vamos pegar o ID da mesma maneira que fizemos anteriormente na rota `get("{id}")`:

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            ...
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Insert user ID to delete a user",
                    status = HttpStatusCode.BadRequest
                )
        }
    }
}
```

- É o mesmo código para pegar o ID que a rota `get("{id}")`. mas a diferença é que usamos um `return@delete` para responder á uma requisição DELETE e a mensagem de erro também é diferente, junto com o seu status, sendo `400 Bad Request`.

```kotlin
fun Route.userRouting(){
    route("/user"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            ...
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Insert user ID to delete a user",
                    status = HttpStatusCode.BadRequest
                    )

             val delete: Int = transaction {
                Users.deleteWhere { Users.id eq id }
             }

            if (delete == 1){
                return@delete call.respondText("Deleted", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("User not found", status = HttpStatusCode.NotFound)
        }
    }
}
```

- Agora usamos novamente o bloco `transaction` para guardar o método para deletar items do banco, que no caso é `Users.deleteWhere{ }`, com o critério do ID do item ser igual á variável ID.
- Tudo isso é guardado dentro da variável `delete`, onde caso essa variável receba `1` como valor, significa que algo foi deletado, e caso receba `0` nada foi deletado.
- Com isso em mente, caso essa variável `delete` receba `1`, vamos responder com um status 200 `Deleted`, pois o usuário foi deletado, e caso seja 0 será respondido com um 404 `User not found` pois nenhum elemento foi deletado, logo esse ID específico não existe no banco de dados.

### Registrando rotas

Agora vamos criar uma função para registrar todas essas rotas ao final do arquivo, dessa maneira:

```kotlin
fun Application.registerArticleRoutes(){
    routing {
        articleRouting()
    }
}
```

Com essa função `Application.registerArticleRoutes` poderemos usar no arquivo `src/main/kotlin/konteudo/plugins/Routing.kt` para registrar essas rotas.

### Rotas Finais

Com isso, teremos essas rotas ao final:

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            val articles = transaction {
                Articles.selectAll().map { Articles.toArticle(it) }
            }

            return@get call.respond(articles)
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                    "Article not found", status = HttpStatusCode.NotFound
            )

            val article: List<Article> = transaction { Articles.select { Articles.id eq id }.map { Articles.toArticle(it) }}

            if (article.isNotEmpty()) {
                return@get call.respond(article.first())
            }
            return@get call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }

        post{
            val article = call.receive<Article>()

            article.id = UUID.randomUUID().toString()

            transaction {
                Articles.insert {
                    it[id] = article.id!!
                    it[name] = article.name
                }
            }

            call.respondText("Created", status = HttpStatusCode.Created)
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Insert article ID to delete a article",
                    status = HttpStatusCode.BadRequest
            )

                val delete: Int = transaction {
                    Articles.deleteWhere { Articles.id eq id }
                }

            if (delete == 1){
                return@delete call.respondText("Deleted", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerArticleRoutes(){
    routing {
        articleRouting()
    }
}
```

### Adicionando rotas `/user` á nossa aplicação

Por enquanto, essas rotas ainda não existem na nossa aplicação, pois é necessário adicionar essas rotas. Pensando nisso, vá até o arquivo `src/main/kotlin/konteudo/plugins/Routing.kt`, e importe a função `registerArticleRoutes` que criamos.

```kotlin
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
+ import konteudo.routes.registerArticleRoutes
```

Agora vamos adicionar a função `registerArticleRoutes()` proximo ao final do arquivo, na função `configureRouting()`:

```kotlin
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
+   registerArticleRoutes()
}
```

Caso você queria, remova esse primeiro bloco `routing` que apenas guarda uma rota GET / que responde apenas um _Hello World_, pois essa rota não será útil no exercício final.

Pronto! Agora caso você inicie a aplicação, todas as rotas relacionadas á usuário, que são:

- GET /user - Lista todos os usuários
- GET /user/id - Responde com um usuário com um ID específico
- POST /user - Insere um novo usuário
- DELETE /user - Remove um usuário

## Criando Models: Article

Agora vamos criar um novo model á nossa aplicação, o model `Article`, que irá representar um artigo na nossa aplicação. Primeiro, vamos criar um arquivo em `src/main/kotlin/konteudo/models/` chamado `Article.kt`.

Vamos fazer alguns imports primeiramente:

```kotlin
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.Serializable
import konteudo.models.Users
import org.jetbrains.exposed.sql.*
```

Agora vamos criar a classe. Cada artigo terá um ID, um título (title), um corpo (body) e um autor (ID do artigo que criou aquele artigo), dessa maneira:

```kotlin
@Serializable
data class Article (var id: String? = null, val title: String, val body: String, val author: String)
```

- `@Serializable` deixa explícito que a classe abaixo poderá ser transformada em JSON e JSON poderá ser transformado nessa classe
- `data class` é usado para classes que apenas irão guardar dados, como é o caso dessa.
- `id` está sendo criado com `var` pois é um campo mutável, podendo assim mudar seu valor, e no seu final, há um `String? = null`, isso é uma maneira de fazer que esse campo também seja opicional para o `kotlinx.serialization`, com seu valor padrão sendo `null` caso não seja preenchido.

Agora teremos de criar uma tabela para guardar esses dados da mesma maneira que fizemos anteriormente com a tabela `articles`:

```kotlin
object Articles: Table(){
    val id: Column<String> = char("id", 36)
    val title: Column<String> = varchar("title", 50)
    val body: Column<String> = text("body")
    val author: Column<String> = char("author_id", 38) references Users.id

    override val primaryKey = PrimaryKey(id, name = "PK_Articles_Id")

    fun toArticle(row: ResultRow): Article = Article(
        id = row[Articles.id],
        title = row[Articles.title],
        body = row[Articles.body],
        author = row[Articles.author],
    )
}
```

### Model Final

Ao final, o arquivo ficará assim:

```kotlin
package com.konteudo.models

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.serialization.Serializable
import konteudo.models.Users
import org.jetbrains.exposed.sql.*

object Articles: Table(){
    val id: Column<String> = char("id", 36)
    val title: Column<String> = varchar("title", 50)
    val body: Column<String> = text("body")
    val author: Column<String> = char("author_id", 38) references Users.id

    override val primaryKey = PrimaryKey(id, name = "PK_Articles_Id")

    fun toArticle(row: ResultRow): Article = Article(
        id = row[Articles.id],
        title = row[Articles.title],
        body = row[Articles.body],
        author = row[Articles.author],
    )
}

@Serializable
data class Article (var id: String? = null, val title: String, val body: String, val author: String)
```

- Podemos ver que há uma clara diferença em relação á tabela `Articles` que haviamos criado, dessa vez temos um atributo `author` que faz uma referência ao atributo `Articles.id`, Logo, sendo uma chave estrangeira. Isso foi feito pois esse atributo guarda o ID do artigo que criou aquele artigo, logo, é um dado que já existe no banco.

### Adicionando Article ao banco de dados

Agora vamos ao arquivo `src/main/kotlin/konteudo/initDB.kt` e vamos adicionar o objeto `Articles` ao banco de dados, para assim, quando o banco de dados iniciar, tanto `Users` quanto `Articles` sejam criados.

Primeiro, importe `Articles` do pacote de `models`.

```kotlin
import konteudo.models.Users
+ import konteudo.models.Articles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
```

Agora, vamos ao bloco `transaction` do arquivo, e vamos adicionar `Articles` ao banco:

```
fun initDB(){
    Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
+       SchemaUtils.create(Articles)
    }
}
```

Pronto, agora, toda vez que a nossa aplicação iniciar, ambas tabelas `users` e `articles` serão criadas.

### initDB Final

ao final, o arquivo `src/main/kotlin/konteudo/initDB.kt` ficará assim:

```kotlin
package konteudo

import konteudo.models.Users
import konteudo.models.Articles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun initDB(){
    Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")

    transaction {
        SchemaUtils.create(Users)
        SchemaUtils.create(Articles)
    }
}
```

## Criando Rotas: Articles

  Agora vamos criar as rotas referentes aos artigos no arquivo `src/main/kotlin/konteudo`. Primeiro, tente criar as 4 rotas, `GET /articles`, `GET /articles/id`, `POST /articles`, `DELETE /articles` sem consultar esse artigo, pois serão os mesmos processos a serem feitos, mas caso tenha dificuldades, volte neste artigo para aprender a como criar essas rotas.

  Já vamos começar esse arquivo fazendo diversas importações de recursos que vamos usar:

```kotlin
import konteudo.models.Article
import konteudo.models.Articles
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID;
```

Agora vamos criar uma função para armazenar as rotas, que pode se chamar por exemplo, `ArticleRouting()`:

```kotlin
fun Route.articleRouting(){
}

```

dentro dessa função, teremos que inserir a rota que será usada, que no caso, vamos chamar de `/article`

```kotlin
fun Route.articleRouting(){
    route("/article"){

    }
}
```

agora, dentro de `route("/article")` poderemos inserir as funções `get()`, `post()`, `delete()`, `put()`, etc. para começar, vamos iniciar um com um GET.

### GET /article

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {

        }
    }
}
```

Pronto, agora pode ser enviado um GET para a rota `/article`. Esse GET irá servir para pegar todos os artigos, e enviar como uma lista como resposta da requisição.

Para pegar essa lista, será necessário usar o Exposed para se comunicar com o banco e pegar essa lista, após isso, teremos que pegar todas as linhas do banco de dados que foram selecionadas, e transformar em instâncias da classe `Article` usando a função `toArticle()`. Com isso em mente, o nosso código ficará assim:

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            val articles = transaction {
                Articles.selectAll().map { Articles.toArticle(it) }
            }
        }
    }
}
```

- No trecho acima, é necessário usar um bloco chamado `transaction` para receber a transação ao banco de dados.
- Dentro desse bloco, selecionamos todas as linhas da tabela `articles`, usando o método `Articles.selectAll()` e transformamos cada linha em uma instância da classe `Article`.
- E uma lista com todos esses dados, é definida na variável `articles`.

Pronto! Agora temos uma lista com todos esses dados, apenas precisamos enviar esses dados como resposta da requisição.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            val articles = transaction {
                Articles.selectAll().map { Articles.toArticle(it) }
            }

            return@get call.respond(articles)
        }
    }
}
```

Para fechar essa primeira rota, vamos usar uma anotação chamada `return@get` que deixa explícito que é o retorno de uma requisição GET.

Para fazer essa resposta, podemos usar a função `call.respond()`, que irá responder a requisição, passando a lista `articles` como objeto.

Essa lista apenas está podendo ser usada como resposta, pois adicionamos a anotação `@Serializable` á classe `Article` e porque temos o **GSON** para fazer a resposta e envio de JSON.

### GET /article/id

Agora vamos criar algo um pouco diferente, vamos criar uma rota que possibilite buscar por um artigo em específico usando o seu ID, com a rota sendo `/article/{id do artigo}`.

Primeiro, precisamos criar uma outra rota GET, que permita que enviamos um ID ao final da URL, isso pode ser feito dessa maneira.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){

        }
    }
}
```

Agora vamos pegar o ID que foi enviado e guardar em uma variável.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                    "Article not found", status = HttpStatusCode.NotFound
            )
        }
    }
}
```

- É definida uma variável chamada `id` que recebe o valor do parâmetro `id` da requisição, usando `call.parameters["id"]`, e usando o operador ternário simplificado `?:`, caso esse ID não seja informado na requisição será enviado um erro 404
- Esse erro 404 será enviado usando um `return@get` para deixar claro que será o retorno da função, e como um 404 pode ser apenas uma resposta em texto, foi usado um `call.respondText`, que necessita de uma mensagem, e um status, que representa o status HTTP da resposta, que no caso é 404.

Após isso, precisamos pegar o artigo com esse ID específico. Podemos fazer isso dessa maneira:

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                    "Article not found", status = HttpStatusCode.NotFound
            )

            val article: List<Article> = transaction { Articles.select { Articles.id eq id }.map { Articles.toArticle(it) }}
        }
    }
}
```

- Criamos uma variável chamada `article`, essa variável vai receber uma lista de instâncias da classe `article`, essa lista poderá armazenar apenas um artigo, pois um ID pertence á só um artigo.
- Para fazer a transação, é usado o bloco `transaction`, e para selecionar linhas de uma tabela usando um critério específico, podemos usar o método `Articles.select {}` informando o critério, que no caso é `Articles.id eq id`, onde necessariamente o ID da linha deve ser igual á variável `id`. Após isso, todas as linhas (uma no caso) são tranformadas em instâncias da classe `Article` com a função `toArticle`.

Agora precisamos responder a requisição, retornando esse artigo em específico.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                    "Article not found", status = HttpStatusCode.NotFound
            )

            val article: List<Article> = transaction { Articles.select { Articles.id eq id }.map { Articles.toArticle(it) }}

            if (article.isNotEmpty()) {
                return@get call.respond(article.first())
            }
            return@get call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }
    }
}
```

- Agora, após a definição da variável `article`, caso essa lista seja vazia (nenhum artigo com certo ID específico) será enviado como resposta um 404, com a mensagem `Article not found` (artigo não encontrado).
- Mas, caso essa lista tenha itens dentro, será enviado como resposta o primeiro artigo, usando `call.respond` para responder com o valor de uma variável, passando `article.first()` que pega o primeiro artigo.

### POST /article

Agora já temos duas rotas para pegar artigos, mas também precisamos criar uma rota que possibilite a criação de novos artigos, no caso, a rota POST.

Primeiro, vamos criar a rota POST:

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{

        }
    }
}
```

Agora vamos usar um recurso do **GSON** e do `@Seriazable` que adicionamos da classe `Article`, vamos transformar o corpo da requisição, em uma instância da classe `Article`, dessa maneira:

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            val article = call.receive<Article>()
        }
    }
}
```

Mas há um dado que não pode ser definido no corpo da requisição, que é o ID desse artigo. Para isso, vamos usar a biblioteca de `UUID` para gerar esse ID único.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            val article = call.receive<Article>()

            article.id = UUID.randomUUID().toString()
        }
    }
}
```

- Usamos o método `UUID.randomUUID()` para gerar um UUID aleatório, e convertemos esse UUID para String usando o `.toString()` ao final.
- Após isso, guardamos esse UUID dentro de `article.id`, para assim, esse artigo ter um ID definido.

Pronto, agora temos essa instância da classe `Article` pronta para ser inserida no banco de dados.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            val article = call.receive<Article>()

            article.id = UUID.randomUUID().toString()

            transaction {
                Articles.insert {
                    it[id] = article.id!!
                    it[title] = article.title
                    it[body] = article.body
                    it[author] = article.author
                }
            }

            call.respondText("Created", status = HttpStatusCode.Created)
        }
    }
}
```

- Agora usamos um outro bloco `transaction` para inserir mais uma linha á tabela `articles`, usando o método `Articles.insert {}` e definindo o valor de cada coluna, como por exemplo `it[id] = article.id`.
- Na linha `it[id] = article.id!!` foram usados dois sinais de exclamação `!!` é para definir o valor de `it[id]` garantindo que o valor de `article.id` não seja nulo. Isso é para garantir que esse valor tenha mudado, pois esse valor inicia-se como nulo.
- Após isso, a requisição é respondida, com um status 201, que representa que algo foi criado no banco de dados.

Pronto! Agora poderemos inserir novos artigos.

### DELETE /article/id

Agora vamos criar uma rota para deletar artigos, sendo essa a última rota que vamos criar agora.

primeiro, vamos criar a rota DELETE.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            ...
        }

        delete("id"){

        }
    }
}
```

Agora vamos pegar o ID da mesma maneira que fizemos anteriormente na rota `get("{id}")`:

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            ...
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Insert article ID to delete a article",
                    status = HttpStatusCode.BadRequest
            )
        }
    }
}
```

- É o mesmo código para pegar o ID que a rota `get("{id}")`. mas a diferença é que usamos um `return@delete` para responder á uma requisição DELETE, uma mensagem que será enviada caso o ID não seja informado e um status `400 Bad Request`, deixando claro que a request não pode ser efetuada com sucesso.

```kotlin
fun Route.articleRouting(){
    route("/article"){
        get {
            ...
        }

        get("{id}"){
            ...
        }

        post{
            ...
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Insert article ID to delete a article",
                    status = HttpStatusCode.BadRequest
            )

            val delete: Int = transaction {
                Articles.deleteWhere { Articles.id eq id }
            }

            if (delete == 1){
                return@delete call.respondText("Deleted", status = HttpStatusCode.OK)
            }
            return@delete call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }
    }
}
```

- Agora usamos novamente o bloco `transaction` para guardar o método para deletar items do banco, que no caso é `Articles.deleteWhere{ }`, com o critério do ID do item ser igual á variável ID.
- Tudo isso é guardado dentro da variável `delete`, onde caso essa variável receba `1` como valor, significa que algo foi deletado, e caso receba `0` nada foi deletado.
- Com isso em mente, caso essa variável `delete` receba `1`, vamos responder com um status 200 `Deleted`, pois o artigo foi deletado, e caso seja 0 será respondido com um 404 `Article not found` pois nenhum elemento foi deletado, logo esse ID específico não existe no banco de dados.

### Rotas Finais

Com isso, teremos essas rotas ao final:

```kotlin
import konteudo.models.Article
import konteudo.models.Articles
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID;

fun Route.articleRouting(){
    route("/article"){
        get {
            val articles = transaction {
                Articles.selectAll().map { Articles.toArticle(it) }
            }

            return@get call.respond(articles)
        }

        get("{id}"){
            val id = call.parameters["id"] ?: return@get call.respondText(
                    "Article not found", status = HttpStatusCode.NotFound
            )

            val article: List<Article> = transaction { Articles.select { Articles.id eq id }.map { Articles.toArticle(it) }}

            if (article.isNotEmpty()) {
                return@get call.respond(article.first())
            }
            return@get call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }

        post{
            val article = call.receive<Article>()

            article.id = UUID.randomUUID().toString()

            transaction {
                Articles.insert {
                    it[id] = article.id
                    it[title] = article.title
                    it[body] = article.body
                    it[author] = article.author
                }
            }

            call.respondText("Created", status = HttpStatusCode.Created)
        }

        delete("id"){
            val id = call.parameters["id"] ?: return@delete call.respondText(
                    "Insert article ID to delete a article",
                    status = HttpStatusCode.BadRequest
            )

            val delete: Int = transaction {
                Articles.deleteWhere { Articles.id eq id }
            }

        if (delete == 1){
            return@delete call.respondText("Deleted", status = HttpStatusCode.OK)
        }
        return@delete call.respondText("Article not found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Application.registerArticleRoutes(){
    routing {
        articleRouting()
    }
}
```

### Adicionando rotas `/article` á nossa aplicação

Por enquanto, essas rotas ainda não existem na nossa aplicação, pois é necessário adicionar essas rotas. Pensando nisso, vá até o arquivo `src/main/kotlin/konteudo/plugins/Routing.kt`, e importe a função `registerArticleRoutes` que criamos.

```kotlin
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import konteudo.routes.registerUserRoutes
+ import konteudo.routes.registerArticleRoutes
```

Agora vamos adicionar a função `registerArticleRoutes()` proximo ao final do arquivo, na função `configureRouting()`:

```kotlin
fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
    registerUserRoutes()
+   registerArticleRoutes()
}
```

## Finalização

Agora temos uma API simplificada de um sistema de artigos, e com esse exercício, aprendemos sobre como funciona o Ktor e como podemos criar aplicações web com ele. Agora, tente adicionar mais funcionalidades nessa aplicação como senhas para usuários, JWT, CORS, etc. por conta própria e a sua escolha.

Muito obrigado por ler este artigo ❤️
