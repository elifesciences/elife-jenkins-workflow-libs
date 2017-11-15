def call(variants, build, stageName='Tests, %s variant') {
    for (int i = 0; i < variants.size(); i++) {
        def variant = variants.get(i)

        stage sprintf(stageName, variant), {
            build(variant)
        }
    }
}
