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
</#macro>
<#macro defined defineMarking>
    clearAllTokens();
    <#list defineMarking.placeBindingList as placeBinding>
        setTokens("${placeBinding.place}", ${placeBinding.value.natLiteral.value});
    </#list>
</#macro>

<#macro expectation expectation>
        // Expecting something.... TODO
</#macro>
<#macro simulation simulation>
    <#list simulation.nameList as transition>
        performTransition("${transition}");
    </#list>
</#macro>