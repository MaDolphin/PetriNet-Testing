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

<#macro expectation expectation>
        // Expecting something.... TODO
</#macro>
<#macro simulation simulation>
    <#list simulation.nameList as transition>
        performTransition("${transition}");
    </#list>
</#macro>