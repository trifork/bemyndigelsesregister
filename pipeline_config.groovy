libraries{
    maven
    docker {
        output_image = '{"name":"bem","image":"registry.fmk.netic.dk/bemyndigelse/bemyndigelsesregister","user":"bemyndigelsesregister"}'
        java_version = 17
    }
    sonarqube_maven
}