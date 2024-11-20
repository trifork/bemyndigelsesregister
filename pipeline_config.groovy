libraries{
    settings { profile = 'fmk-online' }
    maven
    docker {
        output_images = '[{"name":"bem","image":"registry.fmk.netic.dk/bemyndigelse/bemyndigelsesregister","user":"bemyndigelsesregister"}]'
        java_version = 17
    }
    sonarqube_maven
}