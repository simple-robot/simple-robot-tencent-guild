"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[173],{5162:(e,t,n)=>{n.d(t,{Z:()=>i});var a=n(7294),o=n(6010);const r={tabItem:"tabItem_Ymn6"};function i(e){let{children:t,hidden:n,className:i}=e;return a.createElement("div",{role:"tabpanel",className:(0,o.Z)(r.tabItem,i),hidden:n},t)}},4866:(e,t,n)=>{n.d(t,{Z:()=>T});var a=n(7462),o=n(7294),r=n(6010),i=n(2466),l=n(6550),s=n(1980),p=n(7392),m=n(12);function u(e){return function(e){return o.Children.map(e,(e=>{if(!e||(0,o.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}(e).map((e=>{let{props:{value:t,label:n,attributes:a,default:o}}=e;return{value:t,label:n,attributes:a,default:o}}))}function c(e){const{values:t,children:n}=e;return(0,o.useMemo)((()=>{const e=t??u(n);return function(e){const t=(0,p.l)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,n])}function b(e){let{value:t,tabValues:n}=e;return n.some((e=>e.value===t))}function d(e){let{queryString:t=!1,groupId:n}=e;const a=(0,l.k6)(),r=function(e){let{queryString:t=!1,groupId:n}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!n)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return n??null}({queryString:t,groupId:n});return[(0,s._X)(r),(0,o.useCallback)((e=>{if(!r)return;const t=new URLSearchParams(a.location.search);t.set(r,e),a.replace({...a.location,search:t.toString()})}),[r,a])]}function g(e){const{defaultValue:t,queryString:n=!1,groupId:a}=e,r=c(e),[i,l]=(0,o.useState)((()=>function(e){let{defaultValue:t,tabValues:n}=e;if(0===n.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!b({value:t,tabValues:n}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${n.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const a=n.find((e=>e.default))??n[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:t,tabValues:r}))),[s,p]=d({queryString:n,groupId:a}),[u,g]=function(e){let{groupId:t}=e;const n=function(e){return e?`docusaurus.tab.${e}`:null}(t),[a,r]=(0,m.Nk)(n);return[a,(0,o.useCallback)((e=>{n&&r.set(e)}),[n,r])]}({groupId:a}),k=(()=>{const e=s??u;return b({value:e,tabValues:r})?e:null})();(0,o.useLayoutEffect)((()=>{k&&l(k)}),[k]);return{selectedValue:i,selectValue:(0,o.useCallback)((e=>{if(!b({value:e,tabValues:r}))throw new Error(`Can't select invalid tab value=${e}`);l(e),p(e),g(e)}),[p,g,r]),tabValues:r}}var k=n(2389);const v={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function f(e){let{className:t,block:n,selectedValue:l,selectValue:s,tabValues:p}=e;const m=[],{blockElementScrollPositionUntilNextRender:u}=(0,i.o5)(),c=e=>{const t=e.currentTarget,n=m.indexOf(t),a=p[n].value;a!==l&&(u(t),s(a))},b=e=>{let t=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":{const n=m.indexOf(e.currentTarget)+1;t=m[n]??m[0];break}case"ArrowLeft":{const n=m.indexOf(e.currentTarget)-1;t=m[n]??m[m.length-1];break}}t?.focus()};return o.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,r.Z)("tabs",{"tabs--block":n},t)},p.map((e=>{let{value:t,label:n,attributes:i}=e;return o.createElement("li",(0,a.Z)({role:"tab",tabIndex:l===t?0:-1,"aria-selected":l===t,key:t,ref:e=>m.push(e),onKeyDown:b,onClick:c},i,{className:(0,r.Z)("tabs__item",v.tabItem,i?.className,{"tabs__item--active":l===t})}),n??t)})))}function h(e){let{lazy:t,children:n,selectedValue:a}=e;const r=(Array.isArray(n)?n:[n]).filter(Boolean);if(t){const e=r.find((e=>e.props.value===a));return e?(0,o.cloneElement)(e,{className:"margin-top--md"}):null}return o.createElement("div",{className:"margin-top--md"},r.map(((e,t)=>(0,o.cloneElement)(e,{key:t,hidden:e.props.value!==a}))))}function N(e){const t=g(e);return o.createElement("div",{className:(0,r.Z)("tabs-container",v.tabList)},o.createElement(f,(0,a.Z)({},e,t)),o.createElement(h,(0,a.Z)({},e,t)))}function T(e){const t=(0,k.Z)();return o.createElement(N,(0,a.Z)({key:String(t)},e))}},1970:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>m,default:()=>k,frontMatter:()=>p,metadata:()=>u,toc:()=>b});var a=n(7462),o=(n(7294),n(3905)),r=n(4866),i=n(5162),l=n(814),s=n(2965);const p={title:"\u4f7f\u7528SpringBoot",sidebar_position:4},m=void 0,u={unversionedId:"quick-start/spring-boot",id:"quick-start/spring-boot",title:"\u4f7f\u7528SpringBoot",description:"\u901a\u8fc7core\u6a21\u5757\u914d\u5408 simbot starter \u5728 Spring Boot \u4e2d\u8f7b\u677e\u4f7f\u7528\u3002",source:"@site/docs/quick-start/spring-boot.md",sourceDirName:"quick-start",slug:"/quick-start/spring-boot",permalink:"/simbot-component-qq-guild/docs/quick-start/spring-boot",draft:!1,editUrl:"https://github.com/simple-robot/simbot-component-qq-guild/tree/dev/main/website/docs/quick-start/spring-boot.md",tags:[],version:"current",lastUpdatedAt:1683971282,formattedLastUpdatedAt:"2023\u5e745\u670813\u65e5",sidebarPosition:4,frontMatter:{title:"\u4f7f\u7528SpringBoot",sidebar_position:4},sidebar:"tutorialSidebar",previous:{title:"\u4f7f\u7528simbot\u6838\u5fc3\u5e93",permalink:"/simbot-component-qq-guild/docs/quick-start/simbot-core"},next:{title:"BOT\u914d\u7f6e\u6587\u4ef6",permalink:"/simbot-component-qq-guild/docs/bot-config/"}},c={},b=[{value:"\u524d\u63d0\u51c6\u5907",id:"\u524d\u63d0\u51c6\u5907",level:2},{value:"\u9879\u76ee\u6784\u5efa",id:"\u9879\u76ee\u6784\u5efa",level:2},{value:"BOT\u914d\u7f6e",id:"bot\u914d\u7f6e",level:2},{value:"\u542f\u52a8\u7c7b",id:"\u542f\u52a8\u7c7b",level:2},{value:"\u76d1\u542c\u4e8b\u4ef6",id:"\u76d1\u542c\u4e8b\u4ef6",level:2},{value:"\u542f\u52a8",id:"\u542f\u52a8",level:2}],d={toc:b},g="wrapper";function k(e){let{components:t,...n}=e;return(0,o.kt)(g,(0,a.Z)({},d,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("p",null,"\u901a\u8fc7",(0,o.kt)("strong",{parentName:"p"},"core\u6a21\u5757"),"\u914d\u5408 ",(0,o.kt)("strong",{parentName:"p"},"simbot starter")," \u5728 ",(0,o.kt)("strong",{parentName:"p"},"Spring Boot")," \u4e2d\u8f7b\u677e\u4f7f\u7528\u3002"),(0,o.kt)("h2",{id:"\u524d\u63d0\u51c6\u5907"},"\u524d\u63d0\u51c6\u5907"),(0,o.kt)("p",null,"\u9996\u5148\u4f60\u5e94\u5f53\u51c6\u5907\u81f3\u5c11\u4e00\u4e2a\u53ef\u7528\u7684 ",(0,o.kt)("a",{parentName:"p",href:"https://q.qq.com/bot"},"QQ\u9891\u9053\u673a\u5668\u4eba")," \u3002"),(0,o.kt)("h2",{id:"\u9879\u76ee\u6784\u5efa"},"\u9879\u76ee\u6784\u5efa"),(0,o.kt)("p",null,"\u9996\u5148\u51c6\u5907\u4e00\u4e2aSpringBoot\u9879\u76ee\u3002\u53ef\u4ee5\u8003\u8651\u524d\u5f80 ",(0,o.kt)("a",{parentName:"p",href:"https://start.spring.io"},"start.spring.io")," \u6216\u501f\u52a9IDE\u7b49\u5de5\u5177\u3002"),(0,o.kt)("p",null,"\u7136\u540e\u518d\u989d\u5916\u6dfb\u52a0\u6211\u4eec\u9700\u8981\u7684\u4f9d\u8d56: "),(0,o.kt)("admonition",{title:"\u4fdd\u6301\u4f4f",type:"info"},(0,o.kt)("p",{parentName:"admonition"},"\u6ce8\u610f\uff0c\u5728\u4f7f\u7528 Spring Boot \u7684\u65f6\u5019\u4f60\u9700\u8981\u4e00\u4e9b\u80fd\u591f\u4f7f\u7a0b\u5e8f\u4fdd\u6301\u8fd0\u884c\u7684\u7ec4\u4ef6\uff0c\u4f8b\u5982\u901a\u8fc7 ",(0,o.kt)("inlineCode",{parentName:"p"},"spring-web")," \u542f\u7528\u4e00\u4e2a\u670d\u52a1\u5668\uff0c\u5426\u5219\u7a0b\u5e8f\u53ef\u80fd\u4f1a\u81ea\u52a8\u7ec8\u6b62\u3002\n\u56e0\u4e3asimbot\u7684 starter \u5e76\u4e0d\u63d0\u4f9b\u7ef4\u6301\u7a0b\u5e8f\u8fd0\u884c\u7684\u80fd\u529b\u3002"),(0,o.kt)("p",{parentName:"admonition"},"\u4e0b\u8ff0\u793a\u4f8b\u6211\u4eec\u9009\u62e9\u4f7f\u7528 ",(0,o.kt)("inlineCode",{parentName:"p"},"spring-boot-starter-webflux"),"\uff0c\u5177\u4f53\u9009\u62e9\u8bf7\u6839\u636e\u4f60\u7684\u5b9e\u9645\u9700\u6c42\u51b3\u5b9a\u3002")),(0,o.kt)("admonition",{title:"\u6211\u4e5f\u61d2",type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"\u4e0b\u8ff0\u914d\u7f6e\u793a\u4f8b\u57fa\u4e8e ",(0,o.kt)("a",{parentName:"p",href:"https://start.spring.io"},"start.spring.io")," \u751f\u6210\uff0c\u7248\u672c\u53f7\u7b49\u4fe1\u606f\u8bf7\u6839\u636e\u5b9e\u9645\u60c5\u51b5\u505a\u4fee\u6539\u3002")),(0,o.kt)(r.Z,{groupId:"use-dependency",mdxType:"Tabs"},(0,o.kt)(i.Z,{value:"Gradle Kotlin DSL",mdxType:"TabItem"},(0,o.kt)(l.Z,{language:"kotlin",mdxType:"CodeBlock"},`\n// simbot core starter  \nimplementation("love.forte.simbot.boot:simboot-core-spring-boot-starter:$SIMBOT_VERSION") // \u7248\u672c\u8bf7\u53c2\u8003\u4e0b\u6587\u7684\u53c2\u8003\u94fe\u63a5\n// QQ\u9891\u9053\u7ec4\u4ef6  \nimplementation("love.forte.simbot.component:simbot-component-qq-guild-core:${s.i}") // \u6216\u53c2\u8003\u4e0b\u6587\u7684\u53c2\u8003\u94fe\u63a5\n`.trim())),(0,o.kt)(i.Z,{value:"Gradle Groovy",mdxType:"TabItem"},(0,o.kt)(l.Z,{language:"gradle",mdxType:"CodeBlock"},`\n// simbot core starter  \nimplementation 'love.forte.simbot.boot:simboot-core-spring-boot-starter:$SIMBOT_VERSION' // \u7248\u672c\u8bf7\u53c2\u8003\u4e0b\u6587\u7684\u53c2\u8003\u94fe\u63a5\n// QQ\u9891\u9053\u7ec4\u4ef6  \nimplementation 'love.forte.simbot.component:simbot-component-qq-guild-core:${s.i}' // \u6216\u53c2\u8003\u4e0b\u6587\u7684\u53c2\u8003\u94fe\u63a5\n`.trim())),(0,o.kt)(i.Z,{value:"Maven",mdxType:"TabItem"},(0,o.kt)(l.Z,{language:"xml",mdxType:"CodeBlock"},`\n\x3c!-- simbot core starter --\x3e\n<dependency>\n    <groupId>love.forte.simbot.boot</groupId>\n    <artifactId>simboot-core-spring-boot-starter</artifactId>\n    <version>\${SIMBOT_VERSION}</version>\x3c!-- \u7248\u672c\u8bf7\u53c2\u8003\u4e0b\u6587\u7684\u53c2\u8003\u94fe\u63a5 --\x3e\n</dependency>\n\x3c!-- QQ\u9891\u9053\u7ec4\u4ef6 --\x3e\n<dependency>\n    <groupId>love.forte.simbot.component</groupId>\n    <artifactId>simbot-component-qq-guild-core</artifactId>\n    <version>${s.i}</version>\x3c!-- \u6216\u53c2\u8003\u4e0b\u6587\u7684\u53c2\u8003\u94fe\u63a5 --\x3e\n</dependency>\n`.trim()))),(0,o.kt)("admonition",{title:"\u7248\u672c\u53c2\u8003",type:"tip"},(0,o.kt)("ul",{parentName:"admonition"},(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"love.forte.simbot.boot:simboot-core-spring-boot-starter")," ",(0,o.kt)("br",null)," ",(0,o.kt)("a",{parentName:"li",href:"https://github.com/simple-robot/simpler-robot/releases"},(0,o.kt)("strong",{parentName:"a"},"\u7248\u672c\u53c2\u8003"))),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"love.forte.simbot.component:simbot-component-qq-guild-core")," ",(0,o.kt)("br",null)," ",(0,o.kt)("a",{parentName:"li",href:"https://github.com/simple-robot/simbot-component-qq-guild/releases"},(0,o.kt)("strong",{parentName:"a"},"\u7248\u672c\u53c2\u8003"))))),(0,o.kt)("h2",{id:"bot\u914d\u7f6e"},"BOT\u914d\u7f6e"),(0,o.kt)("p",null,"\u63a5\u4e0b\u6765\uff0c\u5728\u9879\u76ee",(0,o.kt)("strong",{parentName:"p"},"\u8d44\u6e90\u6587\u4ef6"),"\u76ee\u5f55\u4e0b\u7684 ",(0,o.kt)("inlineCode",{parentName:"p"},"simbot-bots")," \u6587\u4ef6\u5939\u4e2d\u521b\u5efa\u4e00\u4e2a\u7528\u4e8e\u914d\u7f6ebot\u7684\u914d\u7f6e\u6587\u4ef6 ",(0,o.kt)("inlineCode",{parentName:"p"},"xxx.bot.json")," ( \u6587\u4ef6\u540d\u968f\u610f\uff0c\u6269\u5c55\u540d\u5e94\u4e3a ",(0,o.kt)("inlineCode",{parentName:"p"},".bot")," \u6216 ",(0,o.kt)("inlineCode",{parentName:"p"},".bot.json")," ) \uff0c\n\u800c\u914d\u7f6e\u6587\u4ef6\u7684\u5185\u5bb9\u5219\u53c2\u8003\u7ae0\u8282 ",(0,o.kt)("a",{parentName:"p",href:"../bot-config"},(0,o.kt)("strong",{parentName:"a"},"BOT\u914d\u7f6e\u6587\u4ef6"))," \u3002"),(0,o.kt)("blockquote",null,(0,o.kt)("p",{parentName:"blockquote"},"\u6b64\u8def\u5f84\u4ee5 IDEA \u7684\u9879\u76ee\u7ed3\u6784\u98ce\u683c\u4e3a\u51c6\uff0c\u5982\u679c\u662f\u5176\u4ed6IDE\uff0c\u4f7f\u7528\u5bf9\u5e94\u7684\u8d44\u6e90\u6587\u4ef6\u76ee\u5f55\u3002")),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre"},"${PROJECT_SRC}/main/resources/simbot-bots/xxx.bot.json\n")),(0,o.kt)("admonition",{title:"\u53ef\u914d\u7f6e",type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"\u5982\u679c\u60f3\u8981\u4fee\u6539\u6b64\u8def\u5f84\uff0c\u53ef\u5728 Spring Boot \u7684\u914d\u7f6e\u6587\u4ef6\u4e2d\u8fdb\u884c\u914d\u7f6e\uff1a"),(0,o.kt)(r.Z,{groupId:"spring-boot-config",mdxType:"Tabs"},(0,o.kt)(i.Z,{value:"properties",mdxType:"TabItem"},(0,o.kt)("pre",{parentName:"admonition"},(0,o.kt)("code",{parentName:"pre",className:"language-properties"},"# \u81ea\u5b9a\u4e49\u914d\u7f6ebot\u8d44\u6e90\u6587\u4ef6\u7684\u626b\u63cf\u8def\u5f84\u3002\n# \u9ed8\u8ba4\u4e3a classpath:simbot-bots/*.bot*\n# \u5982\u679c\u8981\u4f7f\u7528\u672c\u5730\u6587\u4ef6\u53ef\u4ee5\u4f7f\u7528 `file:` \u5f00\u5934\nsimbot.bot-configuration-resources[0]=classpath:simbot-bots/*.bot*\n"))),(0,o.kt)(i.Z,{value:"yaml",mdxType:"TabItem"},(0,o.kt)("pre",{parentName:"admonition"},(0,o.kt)("code",{parentName:"pre",className:"language-yaml"},"simbot:\n  \n  # \u81ea\u5b9a\u4e49\u914d\u7f6ebot\u8d44\u6e90\u6587\u4ef6\u7684\u626b\u63cf\u8def\u5f84\u3002\n  # \u9ed8\u8ba4\u4e3a classpath:simbot-bots/*.bot*\n  # \u5982\u679c\u8981\u4f7f\u7528\u672c\u5730\u6587\u4ef6\u53ef\u4ee5\u4f7f\u7528 `file:` \u5f00\u5934\n  bot-configuration-resources:\n    - 'classpath:simbot-bots/*.bot*'\n"))))),(0,o.kt)("h2",{id:"\u542f\u52a8\u7c7b"},"\u542f\u52a8\u7c7b"),(0,o.kt)("p",null,"\u50cf\u6bcf\u4e00\u4e2a Spring Boot \u5e94\u7528\u4e00\u6837\uff0c\u4f60\u9700\u8981\u4e00\u4e2a\u542f\u52a8\u7c7b\uff0c\u5e76\u901a\u8fc7\u6807\u6ce8 ",(0,o.kt)("inlineCode",{parentName:"p"},"@EnableSimbot")," \u6765\u542f\u7528 ",(0,o.kt)("inlineCode",{parentName:"p"},"simbot")," \uff1a"),(0,o.kt)(r.Z,{groupId:"code",mdxType:"Tabs"},(0,o.kt)(i.Z,{value:"Kotlin",mdxType:"TabItem"},(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin",metastring:"title='com.example.App'",title:"'com.example.App'"},"@EnableSimbot\n@SpringBootApplication\nclass App\n\nfun main(vararg args: String) {\n    runApplication<App>(args = args)\n}\n"))),(0,o.kt)(i.Z,{value:"Java",mdxType:"TabItem"},(0,o.kt)("admonition",{title:"\u65e9\u6709\u9884\u9632",type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"\u5982\u679c\u4f60\u5728Java\u4e2d\u9047\u5230\u4e86\u65e0\u6cd5\u5f15\u7528 ",(0,o.kt)("inlineCode",{parentName:"p"},"@EnableSimbot")," \u7b49\u60c5\u51b5\uff0c\u6216\u8bb8\u53ef\u4ee5\u53c2\u8003 ",(0,o.kt)("a",{parentName:"p",href:"https://simbot.forte.love/faq/%E5%8C%85%E5%BC%95%E7%94%A8%E5%BC%82%E5%B8%B8/"},(0,o.kt)("strong",{parentName:"a"},"\u8fd9\u7bc7FAQ")),"\u3002")),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-java",metastring:"title='com.example.App'",title:"'com.example.App'"},"@EnableSimbot\n@SpringBootApplication\npublic class App {\n    public static void main(String[] args) {\n        SpringApplication.run(App.class, args);\n    }\n}\n")))),(0,o.kt)("h2",{id:"\u76d1\u542c\u4e8b\u4ef6"},"\u76d1\u542c\u4e8b\u4ef6"),(0,o.kt)("p",null,"\u63a5\u4e0b\u6765\u5c31\u662f\u903b\u8f91\u4ee3\u7801\u6240\u5728\u7684\u5730\u65b9\u4e86\uff0c\u7f16\u5199\u4e00\u4e2a\u76d1\u542c\u51fd\u6570\u5e76\u76d1\u542c\u4e00\u4e2a\u4e8b\u4ef6\u3002"),(0,o.kt)("p",null,"\u6b64\u5904\u6211\u4eec\u76d1\u542c ",(0,o.kt)("inlineCode",{parentName:"p"},"ChannelMessageEvent"),"\uff0c\u4e5f\u5c31\u662f ",(0,o.kt)("strong",{parentName:"p"},(0,o.kt)("em",{parentName:"strong"},"\u5b50\u9891\u9053\u7684\u6d88\u606f\u4e8b\u4ef6")),"\u3002"),(0,o.kt)("p",null,"\u5047\u8bbe\uff1a\u8981\u6c42bot\u5fc5\u987b",(0,o.kt)("strong",{parentName:"p"},"\u88abAT"),"\uff0c\u5e76\u4e14\u8bf4\u4e00\u53e5 ",(0,o.kt)("inlineCode",{parentName:"p"},"\u4f60\u597d"),"\uff0c\u6b64\u65f6bot\u4f1a",(0,o.kt)("strong",{parentName:"p"},"\u5f15\u7528"),"\u7528\u6237\u53d1\u9001\u7684\u6d88\u606f\u5e76\u56de\u590d ",(0,o.kt)("inlineCode",{parentName:"p"},"\u4f60\u4e5f\u597d!")," \uff0c\u7c7b\u4f3c\u4e8e\uff1a"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-text"},"\u7528\u6237: \n@BOT \u4f60\u597d\n\nBOT:\n> \u7528\u6237: @BOT \u4f60\u597d\n\u4f60\u4e5f\u597d! \n")),(0,o.kt)(r.Z,{groupId:"code",mdxType:"Tabs"},(0,o.kt)(i.Z,{value:"Kotlin",mdxType:"TabItem"},(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin",metastring:"title='com.example.listener.ExampleListener.kt'",title:"'com.example.listener.ExampleListener.kt'"},'import love.forte.simboot.annotation.ContentTrim\nimport love.forte.simboot.annotation.Filter\nimport love.forte.simboot.annotation.Listener\nimport love.forte.simbot.event.ChannelMessageEvent\n\n@Component\nclass ExampleListener {\n    \n    @Listener\n    @Filter(value = "\u4f60\u597d", targets = Filter.Targets(atBot = true))\n    @ContentTrim // \u5f53\u5339\u914d\u88abat\u65f6\uff0c\u5c06\'at\'\u8fd9\u4e2a\u7279\u6b8a\u6d88\u606f\u79fb\u9664\u540e\uff0c\u5269\u4f59\u7684\u6587\u672c\u6d88\u606f\u5927\u6982\u7387\u5b58\u5728\u524d\u540e\u7a7a\u683c\uff0c\u901a\u8fc7\u6b64\u6ce8\u89e3\u5728\u5339\u914d\u7684\u65f6\u5019\u5ffd\u7565\u524d\u540e\u7a7a\u683c\n    suspend fun onChannelMessage(event: ChannelMessageEvent) { // \u5c06\u8981\u76d1\u542c\u7684\u4e8b\u4ef6\u7c7b\u578b\u653e\u5728\u53c2\u6570\u91cc\uff0c\u5373\u4ee3\u8868\u76d1\u542c\u6b64\u7c7b\u578b\u7684\u6d88\u606f\n        event.reply("\u4f60\u4e5f\u597d!")\n    }\n}\n\n\n'))),(0,o.kt)(i.Z,{value:"Java",label:"Java Blocking",mdxType:"TabItem"},(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-java",metastring:"title='com.example.listener.ExampleListener.java'",title:"'com.example.listener.ExampleListener.java'"},'import love.forte.simboot.annotation.ContentTrim\nimport love.forte.simboot.annotation.Filter\nimport love.forte.simboot.annotation.Listener\nimport love.forte.simbot.event.ChannelMessageEvent\n        \n@Component\npublic class ExampleListener {\n    \n    @Listener\n    @Filter(value = "\u4f60\u597d", targets = @Filter.Targets(atBot = true))\n    @ContentTrim // \u5f53\u5339\u914d\u88abat\u65f6\uff0c\u5c06\'at\'\u8fd9\u4e2a\u7279\u6b8a\u6d88\u606f\u79fb\u9664\u540e\uff0c\u5269\u4f59\u7684\u6587\u672c\u6d88\u606f\u5927\u6982\u7387\u5b58\u5728\u524d\u540e\u7a7a\u683c\uff0c\u901a\u8fc7\u6b64\u6ce8\u89e3\u5728\u5339\u914d\u7684\u65f6\u5019\u5ffd\u7565\u524d\u540e\u7a7a\u683c\n    public void onChannelMessage(ChannelMessageEvent event) { // \u5c06\u8981\u76d1\u542c\u7684\u4e8b\u4ef6\u7c7b\u578b\u653e\u5728\u53c2\u6570\u91cc\uff0c\u5373\u4ee3\u8868\u76d1\u542c\u6b64\u7c7b\u578b\u7684\u6d88\u606f\n        // Java\u4e2d\u7684\u963b\u585e\u5f0fAPI\n        event.replyBlocking("\u4f60\u4e5f\u597d!");\n    }\n    \n}\n'))),(0,o.kt)(i.Z,{value:"Java Async",mdxType:"TabItem"},(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-java",metastring:"title='com.example.listener.ExampleListener.java'",title:"'com.example.listener.ExampleListener.java'"},'import love.forte.simboot.annotation.ContentTrim\nimport love.forte.simboot.annotation.Filter\nimport love.forte.simboot.annotation.Listener\nimport love.forte.simbot.event.ChannelMessageEvent\n        \n@Component\npublic class ExampleListener {\n    \n    @Listener\n    @Filter(value = "\u4f60\u597d", targets = @Filter.Targets(atBot = true))\n    @ContentTrim // \u5f53\u5339\u914d\u88abat\u65f6\uff0c\u5c06\'at\'\u8fd9\u4e2a\u7279\u6b8a\u6d88\u606f\u79fb\u9664\u540e\uff0c\u5269\u4f59\u7684\u6587\u672c\u6d88\u606f\u5927\u6982\u7387\u5b58\u5728\u524d\u540e\u7a7a\u683c\uff0c\u901a\u8fc7\u6b64\u6ce8\u89e3\u5728\u5339\u914d\u7684\u65f6\u5019\u5ffd\u7565\u524d\u540e\u7a7a\u683c\n    public CompletableFuture<?> onChannelMessage(ChannelMessageEvent event) { // \u5c06\u8981\u76d1\u542c\u7684\u4e8b\u4ef6\u7c7b\u578b\u653e\u5728\u53c2\u6570\u91cc\uff0c\u5373\u4ee3\u8868\u76d1\u542c\u6b64\u7c7b\u578b\u7684\u6d88\u606f\n        // \u5c06 CompletableFuture \u4f5c\u4e3a\u8fd4\u56de\u503c\uff0csimbot\u4f1a\u4ee5\u975e\u963b\u585e\u7684\u5f62\u5f0f\u5904\u7406\u5b83\n        return event.replyAsync("\u4f60\u4e5f\u597d!");\n    }\n    \n}\n'))),(0,o.kt)(i.Z,{value:"Java Reactive",mdxType:"TabItem"},(0,o.kt)("admonition",{title:"\u6709\u8981\u6c42",type:"tip"},(0,o.kt)("p",{parentName:"admonition"},"\u5982\u679c\u8fd4\u56de\u503c\u662f\u9700\u8981\u7b2c\u4e09\u65b9\u5e93\u7684\u54cd\u5e94\u5f0f\u7c7b\u578b\uff0c\u90a3\u4e48\u4f60\u7684\u9879\u76ee\u73af\u5883\u4f9d\u8d56\u4e2d\u5fc5\u987b\u5b58\u5728 ",(0,o.kt)("inlineCode",{parentName:"p"},"Kotlin courotines")," \u5bf9\u5176\u7684\u652f\u6301\u5e93\u624d\u53ef\u4f7f\u7528\u3002\n\u4f60\u53ef\u4ee5\u53c2\u8003\u6587\u6863\u4e2d  ",(0,o.kt)("a",{parentName:"p",href:"https://simbot.forte.love/docs/basic/event-listener#%E5%8F%AF%E5%93%8D%E5%BA%94%E5%BC%8F%E7%9A%84%E5%A4%84%E7%90%86%E7%BB%93%E6%9E%9C"},(0,o.kt)("em",{parentName:"a"},"\u54cd\u5e94\u5f0f\u7684\u5904\u7406\u7ed3\u679c"))," \u7684\u5185\u5bb9\u3002")),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-java",metastring:"title='com.example.listener.ExampleListener.java'",title:"'com.example.listener.ExampleListener.java'"},'import love.forte.simboot.annotation.ContentTrim\nimport love.forte.simboot.annotation.Filter\nimport love.forte.simboot.annotation.Listener\nimport love.forte.simbot.event.ChannelMessageEvent\n        \n@Component\npublic class ExampleListener {\n    \n    @Listener\n    @Filter(value = "\u4f60\u597d", targets = @Filter.Targets(atBot = true))\n    @ContentTrim // \u5f53\u5339\u914d\u88abat\u65f6\uff0c\u5c06\'at\'\u8fd9\u4e2a\u7279\u6b8a\u6d88\u606f\u79fb\u9664\u540e\uff0c\u5269\u4f59\u7684\u6587\u672c\u6d88\u606f\u5927\u6982\u7387\u5b58\u5728\u524d\u540e\u7a7a\u683c\uff0c\u901a\u8fc7\u6b64\u6ce8\u89e3\u5728\u5339\u914d\u7684\u65f6\u5019\u5ffd\u7565\u524d\u540e\u7a7a\u683c\n    public Mono<?> onChannelMessage(ChannelMessageEvent event) { // \u5c06\u8981\u76d1\u542c\u7684\u4e8b\u4ef6\u7c7b\u578b\u653e\u5728\u53c2\u6570\u91cc\uff0c\u5373\u4ee3\u8868\u76d1\u542c\u6b64\u7c7b\u578b\u7684\u6d88\u606f\n        // \u5c06 Mono \u7b49\u54cd\u5e94\u5f0f\u7c7b\u578b\u4f5c\u4e3a\u8fd4\u56de\u503c\uff0csimbot\u4f1a\u4ee5\u975e\u963b\u585e\u7684\u5f62\u5f0f\u5904\u7406\u5b83\n        return Mono.fromCompletionStage(event.replyAsync("\u4f60\u4e5f\u597d!"));\n    }\n    \n}\n')))),(0,o.kt)("h2",{id:"\u542f\u52a8"},"\u542f\u52a8"),(0,o.kt)("p",null,"\u63a5\u4e0b\u6765\uff0c\u542f\u52a8\u7a0b\u5e8f\u5e76\u5728\u4f60\u7684\u6c99\u7bb1\u9891\u9053\u4e2d@\u5b83\u8bd5\u8bd5\u770b\u5427\u3002"),(0,o.kt)("p",null,"\u5f53\u7136\uff0c\u5982\u679c\u9047\u5230\u4e86\u9884\u671f\u5916\u7684\u95ee\u9898\u4e5f\u4e0d\u8981\u614c\uff0c\u79ef\u6781\u53cd\u9988\u95ee\u9898\u624d\u80fd\u4f7f\u6211\u4eec\u53d8\u5f97\u66f4\u597d\uff0c\u53ef\u4ee5\u524d\u5f80 ",(0,o.kt)("a",{parentName:"p",href:"https://github.com/simple-robot/simpler-robot/issues"},"Issues")," \u53cd\u9988\u95ee\u9898\u3001",(0,o.kt)("a",{parentName:"p",href:"https://github.com/orgs/simple-robot/discussions"},"\u793e\u533a")," \u63d0\u51fa\u7591\u95ee\u3002"))}k.isMDXComponent=!0},2965:e=>{e.exports=JSON.parse('{"i":"3.0.0.0-alpha.6"}')}}]);