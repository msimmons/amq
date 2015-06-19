package com.gazelle.amq

import com.gazelle.amq.route.WiretapProcessor
import spock.lang.Specification

/**
 * Created by msimmons on 4/16/15.
 */
class WiretapSpec extends Specification {

    def "Convert the json to a mongo object"() {

        given:
        WiretapProcessor processor = new WiretapProcessor()

        expect:
        processor.save('{"layeredCost":{ "srNumber" : "SR2102398" , "costType" : "FOO" , "cost" : [ "3" , "4"] , "nestedElement" : { "@anAttribute" : "att" , "foo" : "bar" , "bar" : "foo"}}}')
    }
}
