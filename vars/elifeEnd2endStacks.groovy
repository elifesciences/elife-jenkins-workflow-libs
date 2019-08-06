def call() {
    return [
        'annotations--end2end',
        'api-gateway--end2end',
        'bioprotocol--end2end',
        'digests--end2end',
        'elife-bot--end2end',
        'elife-dashboard--end2end',
        'elife-metrics--end2end',
        'elife-xpub--end2end',
        'iiif--end2end',
        'journal--end2end',
        'journal-cms--end2end',
        'lax--end2end',
        'medium--end2end',
        'observer--end2end',
        // excluded because it does not interact with other end2end projects,
        // nor it uses spectrum tests
        //'peerscout--end2end',
        'personalised-covers--end2end',
        'profiles--end2end',
        'recommendations--end2end',
        'search--end2end'
    ]
}
