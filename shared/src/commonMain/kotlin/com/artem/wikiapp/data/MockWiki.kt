package com.artem.wikiapp.data

data class WikiPage(
    val pageid: Long,
    val title: String,
    val extract: String,
    val fullurl: String,
    val length: Int,
    val touched: String,
    val categories: List<Category>,
    val links: List<PageLink>,
)

data class Category(
    val title: String
)

data class PageLink(
    val title: String
)

val mockWikiPages: List<WikiPage> = listOf(
    WikiPage(
        pageid = 26032057,
        title = "Kotlin (programming language)",
        extract = "Kotlin is a cross-platform, statically typed, general-purpose high-level programming language with type inference. Kotlin is designed to interoperate fully with Java, and the JVM version of Kotlin's standard library depends on the Java Class Library.",
        fullurl = "https://en.wikipedia.org/wiki/Kotlin_(programming_language)",
        length = 42150,
        categories = listOf(Category("Category:Kotlin (programming language)"), Category("Category:Cross-platform software"), Category("Category:JVM programming languages")),
        touched = "2026-08-15T12:30:00Z",
        links = listOf(PageLink("Java (programming language)"), PageLink("Android (operating system)"))
    ),
    WikiPage(
        pageid = 202613,
        title = "Android (operating system)",
        extract = "Android is a mobile operating system based on a modified version of the Linux kernel and other open-source software, designed primarily for touchscreen mobile devices such as smartphones and tablets.",
        fullurl = "https://en.wikipedia.org/wiki/Android_(operating_system)",
        length = 135400,
        categories = listOf(Category("Category:Android (operating system)"), Category("Category:Mobile operating systems"), Category("Category:Linux distributions")),
        touched = "2026-08-20T09:14:22Z",
        links = listOf(PageLink("Linux"), PageLink("Operating system"))
    ),
    WikiPage(
        pageid = 18087,
        title = "Linux",
        extract = "Linux is a family of open-source Unix-like operating systems based on the Linux kernel, an operating system kernel first released on September 17, 1991, by Linus Torvalds.",
        fullurl = "https://en.wikipedia.org/wiki/Linux",
        length = 112800,
        categories = listOf(Category("Category:Linux"), Category("Category:Free software operating systems"), Category("Category:Unix variants")),
        touched = "2026-08-21T18:45:10Z",
        links = listOf(PageLink("Operating system"), PageLink("Open-source software"))
    ),
    WikiPage(
        pageid = 5323,
        title = "Computer science",
        extract = "Computer science is the study of computation, information, and automation. Computer science spans theoretical disciplines to practical disciplines including the design and implementation of hardware and software.",
        fullurl = "https://en.wikipedia.org/wiki/Computer_science",
        length = 88200,
        categories = listOf(Category("Category:Computer science"), Category("Category:Computing"), Category("Category:Academic disciplines")),
        touched = "2026-08-19T14:10:05Z",
        links = listOf(PageLink("Algorithm"), PageLink("Artificial intelligence"))
    ),
    WikiPage(
        pageid = 1164,
        title = "Artificial intelligence",
        extract = "Artificial intelligence (AI) is the intelligence of machines or software, as opposed to the intelligence of living beings, primarily of humans. It is a field of study in computer science that develops and studies intelligent machines.",
        fullurl = "https://en.wikipedia.org/wiki/Artificial_intelligence",
        length = 164300,
        categories = listOf(Category("Category:Artificial intelligence"), Category("Category:Cybernetics"), Category("Category:Cognitive science")),
        touched = "2026-08-22T11:02:40Z",
        links = listOf(PageLink("Computer science"), PageLink("Algorithm"))
    ),
    WikiPage(
        pageid = 33248,
        title = "Wikipedia",
        extract = "Wikipedia is a free online encyclopedia written and maintained by a community of volunteers, known as Wikipedians, through open collaboration and using a wiki-based editing system called MediaWiki.",
        fullurl = "https://en.wikipedia.org/wiki/Wikipedia",
        length = 195000,
        categories = listOf(Category("Category:Wikipedia"), Category("Category:Internet encyclopedias"), Category("Category:Crowdsourced projects")),
        touched = "2026-08-22T04:15:33Z",
        links = listOf(PageLink("World Wide Web"), PageLink("Hypertext Transfer Protocol"))
    ),
    WikiPage(
        pageid = 23862,
        title = "Python (programming language)",
        extract = "Python is a high-level, general-purpose programming language. Its design philosophy emphasizes code readability with the use of significant indentation.",
        fullurl = "https://en.wikipedia.org/wiki/Python_(programming_language)",
        length = 94300,
        categories = listOf(Category("Category:Python (programming language)"), Category("Category:Object-oriented programming languages"), Category("Category:Dynamically typed programming languages")),
        touched = "2026-08-18T16:20:11Z",
        links = listOf(PageLink("Computer science"), PageLink("Software architecture"))
    ),
    WikiPage(
        pageid = 15881,
        title = "Java (programming language)",
        extract = "Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.",
        fullurl = "https://en.wikipedia.org/wiki/Java_(programming_language)",
        length = 105600,
        categories = listOf(Category("Category:Java programming language"), Category("Category:JVM programming languages"), Category("Category:Class-based programming languages")),
        touched = "2026-08-17T21:05:50Z",
        links = listOf(PageLink("Kotlin (programming language)"), PageLink("Android (operating system)"))
    ),
    WikiPage(
        pageid = 1326462,
        title = "Git",
        extract = "Git is a distributed version control system that tracks changes in any set of computer files, usually used for coordinating work among programmers who are collaboratively developing source code during software development.",
        fullurl = "https://en.wikipedia.org/wiki/Git",
        length = 52100,
        categories = listOf(Category("Category:Git"), Category("Category:Distributed version control systems"), Category("Category:Free software programmed in C")),
        touched = "2026-08-14T08:44:00Z",
        links = listOf(PageLink("Linux"), PageLink("Open-source software"))
    ),
    WikiPage(
        pageid = 13612,
        title = "Hypertext Transfer Protocol",
        extract = "The Hypertext Transfer Protocol (HTTP) is an application layer protocol in the Internet protocol suite model for distributed, collaborative, hypermedia information systems.",
        fullurl = "https://en.wikipedia.org/wiki/Hypertext_Transfer_Protocol",
        length = 61200,
        categories = listOf(Category("Category:Hypertext Transfer Protocol"), Category("Category:Application layer protocols"), Category("Category:Web standards")),
        touched = "2026-08-10T13:12:19Z",
        links = listOf(PageLink("World Wide Web"), PageLink("Computer science"))
    ),
    WikiPage(
        pageid = 33139,
        title = "World Wide Web",
        extract = "The World Wide Web (WWW), commonly known as the Web, is an information system that enables content sharing over the Internet through user-friendly ways, such as web pages connected by hyperlinks.",
        fullurl = "https://en.wikipedia.org/wiki/World_Wide_Web",
        length = 84100,
        categories = listOf(Category("Category:World Wide Web"), Category("Category:Internet history"), Category("Category:Hypertext systems")),
        touched = "2026-08-16T19:50:00Z",
        links = listOf(PageLink("Hypertext Transfer Protocol"), PageLink("Wikipedia"))
    ),
    WikiPage(
        pageid = 792,
        title = "Algorithm",
        extract = "In mathematics and computer science, an algorithm is a finite sequence of rigorous instructions, typically used to solve a class of specific problems or to perform a computation.",
        fullurl = "https://en.wikipedia.org/wiki/Algorithm",
        length = 73500,
        categories = listOf(Category("Category:Algorithms"), Category("Category:Theoretical computer science"), Category("Category:Mathematical logic")),
        touched = "2026-08-12T10:00:15Z",
        links = listOf(PageLink("Computer science"), PageLink("Artificial intelligence"))
    ),
    WikiPage(
        pageid = 8436,
        title = "Database",
        extract = "In computing, a database is an organized collection of data or a type of data store based on the use of a database management system (DBMS), the software that interacts with end users, applications, and the database itself.",
        fullurl = "https://en.wikipedia.org/wiki/Database",
        length = 68900,
        categories = listOf(Category("Category:Databases"), Category("Category:Data management"), Category("Category:Information retrieval")),
        touched = "2026-08-11T15:33:41Z",
        links = listOf(PageLink("Cloud computing"), PageLink("Computer science"))
    ),
    WikiPage(
        pageid = 1541306,
        title = "Cloud computing",
        extract = "Cloud computing is the on-demand availability of computer system resources, especially data storage and computing power, without direct active management by the user.",
        fullurl = "https://en.wikipedia.org/wiki/Cloud_computing",
        length = 91200,
        categories = listOf(Category("Category:Cloud computing"), Category("Category:Distributed computing"), Category("Category:Virtualization")),
        touched = "2026-08-19T07:22:04Z",
        links = listOf(PageLink("Database"), PageLink("Open-source software"))
    ),
    WikiPage(
        pageid = 22002,
        title = "Open-source software",
        extract = "Open-source software (OSS) is computer software that is released under a license in which the copyright holder grants users the rights to use, study, change, and distribute the software and its source code to anyone and for any purpose.",
        fullurl = "https://en.wikipedia.org/wiki/Open-source_software",
        length = 77800,
        categories = listOf(Category("Category:Open-source software"), Category("Category:Free software movement"), Category("Category:Software distribution")),
        touched = "2026-08-13T17:18:29Z",
        links = listOf(PageLink("Linux"), PageLink("Git"))
    ),
    WikiPage(
        pageid = 28212,
        title = "Software architecture",
        extract = "Software architecture refers to the fundamental structures of a software system and the discipline of creating such structures and systems. Each structure comprises software elements, relations among them, and properties of both elements and relations.",
        fullurl = "https://en.wikipedia.org/wiki/Software_architecture",
        length = 49800,
        categories = listOf(Category("Category:Software architecture"), Category("Category:Software engineering"), Category("Category:System architecture")),
        touched = "2026-08-08T12:01:50Z",
        links = listOf(PageLink("Computer science"), PageLink("User interface"))
    ),
    WikiPage(
        pageid = 32258,
        title = "User interface",
        extract = "In the industrial design field of human-computer interaction, a user interface (UI) is the space where interactions between humans and machines occur.",
        fullurl = "https://en.wikipedia.org/wiki/User_interface",
        length = 38400,
        categories = listOf(Category("Category:User interfaces"), Category("Category:Human–computer interaction"), Category("Category:Design")),
        touched = "2026-08-09T09:40:02Z",
        links = listOf(PageLink("Software architecture"), PageLink("Operating system"))
    ),
    WikiPage(
        pageid = 22283,
        title = "Operating system",
        extract = "An operating system (OS) is system software that manages computer hardware, software resources, and provides common services for computer programs.",
        fullurl = "https://en.wikipedia.org/wiki/Operating_system",
        length = 82900,
        categories = listOf(Category("Category:Operating systems"), Category("Category:System software"), Category("Category:Computing infrastructure")),
        touched = "2026-08-14T23:00:12Z",
        links = listOf(PageLink("Linux"), PageLink("Android (operating system)"))
    ),
    WikiPage(
        pageid = 6182,
        title = "Compiler",
        extract = "In computing, a compiler is a computer program that translates computer code written in one programming language (the source language) into another language (the target language).",
        fullurl = "https://en.wikipedia.org/wiki/Compiler",
        length = 59100,
        categories = listOf(Category("Category:Compilers"), Category("Category:Programming language implementation"), Category("Category:Translators")),
        touched = "2026-08-05T18:11:30Z",
        links = listOf(PageLink("Kotlin (programming language)"), PageLink("Swift (programming language)"))
    ),
    WikiPage(
        pageid = 42967116,
        title = "Swift (programming language)",
        extract = "Swift is a high-level compiled programming language developed by Apple Inc. and the open-source community. Swift was designed as a replacement for Apple's earlier programming language Objective-C.",
        fullurl = "https://en.wikipedia.org/wiki/Swift_(programming_language)",
        length = 51200,
        categories = listOf(Category("Category:Swift (programming language)"), Category("Category:Apple Inc. software"), Category("Category:Compiled programming languages")),
        touched = "2026-08-21T06:22:15Z",
        links = listOf(PageLink("Compiler"), PageLink("Java (programming language)"))
    )
)