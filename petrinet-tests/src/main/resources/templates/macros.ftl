<#macro initial initialMarking>
    <#if (initialMarking.isPresentInheritMarking())>
<@inherited inheritMarking=initialMarking.inheritMarking/>
    </#if>
    <#if (initialMarking.isPresentDefineMarking())>
<@defined defineMarking=initialMarking.defineMarking/>
    </#if>
</#macro>
<#macro inherited inheritMarking>
        // Using inherited initial marking
<@rest marking=inheritMarking/>
</#macro>
<#macro defined defineMarking>
        clearAllTokens();
    <#list defineMarking.placeBindingList as placeBinding>
    <#-- TODO: Setting a token to 0 and then applying rest sets that place to rest -->
        setTokens("${placeBinding.place}", ${placeBinding.value.natLiteral.value});
    </#list>
<@rest marking=defineMarking/>
</#macro>
<#macro rest marking>
        <#if (marking.isPresentRestSpecification())>
        applyRest(${marking.restSpecification.markingValue.natLiteral.value});  
        </#if>
</#macro>

<#macro mcondition condition>
    <#assign class=(condition.class.name)!'unknown'/>
    <#if (class?contains('ASTNegation'))>
        !(
        <@mcondition condition.condition/>
        ) //not
    <#elseif (class?contains('ASTConjunction'))>
        all(
            <#list condition.getConditionList() as subCondition>
<@mcondition subCondition/><#sep>,</#sep>
            </#list>
        ) //all
    <#elseif (class?contains('ASTDisjunction'))>
        any(
            <#list condition.getConditionList() as subCondition>
<@mcondition subCondition/><#sep>,</#sep>
            </#list>
        ) //any
    <#elseif (class?contains('ASTMarkingCondition'))>
        all(
        <#list condition.placeBindingList as placeBinding>
            checkMarking("${placeBinding.place}", ${placeBinding.value.natLiteral.value})<#sep>,</#sep>
        </#list>
        ) //all-ma
    <#elseif (class?contains('ASTEnabledCondition'))>
        all(
        <#list condition.nameList as name>
            checkEnabled("${name}")<#sep>,</#sep>
        </#list>
        ) //all-en
    <#else>
    </#if>
</#macro>

<#macro expectation expectation>
        assertTrue(
<@mcondition condition=expectation.condition/>
        ); //at
</#macro>
<#macro simulation simulation>
    <#list simulation.nameList as transition>
        performTransition("${transition}");
    </#list>
</#macro>