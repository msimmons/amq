package com.gazelle.amq

import com.gazelle.amq.service.GroupIdConfig
import com.gazelle.amq.service.GroupIdService
import spock.lang.Specification
import spock.lang.Unroll
/**
 * Created by msimmons on 6/12/15.
 */
class GroupIdServiceSpec extends Specification {

    @Unroll
    def "Calculate the group id appropriately"() {
        setup:
        def config1 = new GroupIdConfig('erp.item', 'srNumber', 10)
        def config2 = new GroupIdConfig('erp.item', 'rmaReceived', 'rmaNumber', 10)
        Map<String,GroupIdConfig> configMap = new HashMap<>()
        configMap.put(config1.getMapKey(), config1)
        configMap.put(config2.getMapKey(), config2)
        println configMap
        GroupIdService service = new GroupIdService.Impl(configMap: configMap)

        expect:
        service.getGroupId(destination, message) == gid

        where:
        destination|message|gid
        'erp.item'|'<?xml something something?><itemUnpacked><srNumber>SR1</srNumber></itemUnpacked>'|5
        'erp.item'|'<itemUnpacked><srNumber>SR2</srNumber></itemUnpacked>'|6
        'erp.item'|'<rmaReceived><srNumber>SR2</srNumber><rmaNumber>RMA55555</rmaNumber></rmaReceived>'|4
        'erp.item'|'<itemReceived><facility>GZ_LOUISVILLE</facility><srNumber>SR3</srNumber><productId>12345</productId></itemReceived>'|7
        'erp.item'|'<itemPacked>\n' +
            '<srNumber>SR120398</srNumber>\n' +
            '<disposition>RETAIL</disposition>\n' +
            '</itemPacked>'|9

    }
}
