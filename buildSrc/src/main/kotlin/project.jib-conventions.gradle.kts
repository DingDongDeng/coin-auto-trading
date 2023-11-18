plugins {
    id("com.google.cloud.tools.jib")
}

jib { // https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin
    from {
        //https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#from-closure
        //https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin#setting-the-base-image
        image = "eclipse-temurin:17-jre"
    }
    container {
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}