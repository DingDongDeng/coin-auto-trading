plugins {
    id("project.kotlin-conventions")
    id("io.spring.dependency-management")
    id("org.asciidoctor.jvm.convert")
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}