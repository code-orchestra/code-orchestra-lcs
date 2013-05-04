package codeOrchestra.lcs.digest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import scala.Option;
import scala.Symbol;
import scala.collection.Iterator;
import scala.collection.immutable.List;
import apparat.abc.Abc;
import apparat.abc.AbcClass;
import apparat.abc.AbcInstance;
import apparat.abc.AbcMethod;
import apparat.abc.AbcMethodParameter;
import apparat.abc.AbcName;
import apparat.abc.AbcNamespace;
import apparat.abc.AbcNamespaceKind;
import apparat.abc.AbcQName;
import apparat.abc.AbcScript;
import apparat.abc.AbcTrait;
import apparat.abc.AbcTraitClass;
import apparat.abc.AbcTraitConst;
import apparat.abc.AbcTraitGetter;
import apparat.abc.AbcTraitKind;
import apparat.abc.AbcTraitMethod;
import apparat.abc.AbcTraitSetter;
import apparat.abc.AbcTraitSlot;
import apparat.abc.AbcTypename;
import apparat.swf.DoABC;
import apparat.swf.SwfTag;
import apparat.utils.TagContainer;
import codeOrchestra.utils.NameUtil;
import codeOrchestra.utils.StringUtils;
import codeOrchestra.utils.XMLUtils;

/**
 * @author Alexander Eliseyev
 */
public class SWCDigest {

  public static String VECTOR_PACKAGE = "__AS3__.vec";

  private java.util.List<String> swcPaths;
  private String outputPath;

  private Map<String, Document> digestsMap = new HashMap<String, Document>();

  public SWCDigest(java.util.List<String> swcPaths, String outputPath) {
    this.swcPaths = swcPaths;
    this.outputPath = outputPath;
  }

  public void generate() {
    for (String swcPath : swcPaths) {
      digestsMap.clear();
      init(swcPath);
      save(new File(swcPath).getName());
    }
  }

  private void save(String swcName) {
    for (String packName : digestsMap.keySet()) {
      Document document = digestsMap.get(packName);
      try {
        File outputDir = new File(outputPath, swcName);
        if (!outputDir.exists()) {
          outputDir.mkdirs();
        }

        XMLUtils.saveToFile(new File(outputPath, swcName + File.separator + packName + ".digest").getPath(), document);
      } catch (TransformerException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private void init(String swcPath) {
    File swcFile = new File(swcPath);
    TagContainer tagContainer = TagContainer.fromFile(swcFile);

    List<SwfTag> tagsScalaList = tagContainer.tags();
    Iterator tagsScalaIterator = tagsScalaList.iterator();

    while (tagsScalaIterator.hasNext()) {
      SwfTag tag = (SwfTag) tagsScalaIterator.next();
      if (tag instanceof DoABC) {
        DoABC doABC = (DoABC) tag;
        Abc abc = Abc.fromDoABC(doABC);

        AbcScript[] abcScripts = abc.scripts();
        for (AbcScript abcScript : abcScripts) {
          AbcTrait[] abcTraits = abcScript.traits();
          for (AbcTrait abcTrait : abcTraits) {
            cacheTrait(abcTrait);
          }
        }
      }
    }
  }

  private void cacheTrait(AbcTrait trait) {
    String traitName = trait.name().name().name;
    String traitPackage = trait.name().namespace().name().name;
    Document document = getOrCreateDocument(traitPackage);
    Element rootElement = (Element) document.getFirstChild();
    
    // Move Vector to the default package
    if (VECTOR_PACKAGE.equals(traitPackage)) {
      if (!traitName.equals("Vector")) {
        return;
      }

      traitPackage = "";
    }
    
    // FQ Name
    String fqName;
    if (traitPackage == null || "".equals(traitPackage.trim())) {
      fqName = traitName;
    } else {
      fqName = traitPackage + "." + traitName;
    }
    
    // Namespaces
    if (trait.kind() == AbcTraitKind.Const()) {
      AbcTraitConst traitConst = (AbcTraitConst) trait;
      
      Option<Object> valueOption = traitConst.value();
      if (!(valueOption.isEmpty())) { 
        if (valueOption.get() instanceof AbcNamespace) {
          AbcNamespace abcNamespace = (AbcNamespace) valueOption.get();
          
          Element namespaceElement = document.createElement("namespace");
          namespaceElement.setAttribute("name", traitName);
          namespaceElement.setAttribute("fqName", fqName);
          namespaceElement.setAttribute("uri", abcNamespace.name().name);
          
          rootElement.appendChild(namespaceElement);
        }
      }      
    }

    // Classes
    if (trait.kind() != AbcTraitKind.Class()) {
      return;
    }
    // Skip interfaces
    AbcTraitClass traitClass = (AbcTraitClass) trait;
    if (traitClass.nominalType().inst().isInterface()) {
      return;
    }
    
    Element traitElement = document.createElement("trait");
    traitElement.setAttribute("fqName", fqName);
    rootElement.appendChild(traitElement);

    // Superclass
    AbcInstance abcInstance = traitClass.nominalType().inst();
    Option<AbcName> baseOption = abcInstance.base();
    if (!baseOption.isEmpty()) {
      AbcQName baseName = ((AbcQName) baseOption.get());
      String packageName = MultiNameUtil.getNamespace(baseName);
      String typeName = MultiNameUtil.getName(baseName);
      String superFqName = NameUtil.longNameFromNamespaceAndShortName(packageName, typeName);

      traitElement.setAttribute("superFqName", superFqName);
    }

    // Members
    java.util.List<Member> members = getMembers(traitClass);
    for (Member member : members) {
      Element memberElement = document.createElement("member");
      memberElement.setAttribute("name", member.getName());
      memberElement.setAttribute("type", member.getType());
      memberElement.setAttribute("kind", member.getKind().name());
      if (member.isStatic()) {
        memberElement.setAttribute("static", Boolean.TRUE.toString());
      }
      memberElement.setAttribute("visibility", member.getVisibility().name());
      for (Parameter parameter : member.getParameters()) {
        Element parameterElement = document.createElement("parameter");
        parameterElement.setAttribute("name", parameter.getName());
        parameterElement.setAttribute("type", parameter.getType());
        memberElement.appendChild(parameterElement);
      }
      traitElement.appendChild(memberElement);
    }
  }

  private void extractMemberName(AbcTrait trait, java.util.List<Member> result, boolean isStatic) {
    // Skip private members
    int namespaceKind = trait.name().namespace().kind();
    if (namespaceKind == AbcNamespaceKind.Private()) {
      return;
    }

    int traitKind = trait.kind();
    MemberKind memberKind = MemberKind.byTraitKind(traitKind);
    if (traitKind == AbcTraitKind.Method() || traitKind == AbcTraitKind.Getter() || traitKind == AbcTraitKind.Setter()) {
      // Methods
      String methodName;
      AbcMethod methodInfo;
      if (traitKind == AbcTraitKind.Method()) {
        AbcTraitMethod traitMethod = (AbcTraitMethod) trait;
        methodInfo = traitMethod.method();
        methodName = traitMethod.name().name().name;
      } else if (traitKind == AbcTraitKind.Getter()) {
        AbcTraitGetter traitGetter = (AbcTraitGetter) trait;
        methodInfo = traitGetter.method();
        methodName = traitGetter.name().name().name;
      } else if (traitKind == AbcTraitKind.Setter()) {
        AbcTraitSetter traitSetter = (AbcTraitSetter) trait;
        methodInfo = traitSetter.method();
        methodName = traitSetter.name().name().name;
      } else {
        return;
      }            
      Member member = new Member(methodName, getType(methodInfo.returnType()), isStatic, memberKind, getVisibility(trait));
      int i = 0;
      for (AbcMethodParameter p : methodInfo.parameters()) {
        String parameterName = null; 
        
        Option<Symbol> nameOption = p.name(); 
        if (!(nameOption.isEmpty())) { 
          Symbol nameSymbol = nameOption.get(); 
          if (nameSymbol != null) { 
            parameterName = nameSymbol.name; 
          } 
        } 
        if (parameterName == null) { 
          parameterName = "arg" + (i++); 
        }
        
        member.addParameter(parameterName, getType(p.typeName()));
      }
      if (!result.contains(member)) {
        result.add(member);
      }
    } else if (traitKind == AbcTraitKind.Const() || traitKind == AbcTraitKind.Slot()) {
      String fieldName;
      String typeName;
      if (traitKind == AbcTraitKind.Const()) {
        AbcTraitConst traitConst = (AbcTraitConst) trait;
        typeName = getType(traitConst.typeName());
        fieldName = traitConst.name().name().name;
      } else if (traitKind == AbcTraitKind.Slot()) {
        AbcTraitSlot traitSlot = (AbcTraitSlot) trait;
        typeName = getType(traitSlot.typeName());
        fieldName = traitSlot.name().name().name;
      } else {
        return;
      }
      Member member = new Member(fieldName, typeName, isStatic, memberKind, getVisibility(trait));
      if (!result.contains(member)) {
        result.add(member);
      }
    }
  }

  private String getType(AbcName typeInfo) {
    if (typeInfo instanceof AbcTypename) {
      return "Vector";
    }
    
    String targetPackage = MultiNameUtil.getNamespace(typeInfo); 
    String targetType = MultiNameUtil.getName(typeInfo);
    
    if (StringUtils.isEmpty(targetType) || "*".equals(targetType)) {
      return "*";
    }
    if (StringUtils.isEmpty(targetPackage)) {
      return targetType;
    }
    return NameUtil.longNameFromNamespaceAndShortName(targetPackage, targetType);
  }
  
  private Visibility getVisibility(AbcTrait trait) {
    int namespaceKind = trait.name().namespace().kind();
    
    if (namespaceKind == AbcNamespaceKind.Private()) { 
      return Visibility.PRIVATE; 
    } else if (namespaceKind == AbcNamespaceKind.PackageInternal()) { 
      return Visibility.INTERNAL; 
    } else if (namespaceKind == AbcNamespaceKind.Protected() || namespaceKind == AbcNamespaceKind.StaticProtected()) { 
      return Visibility.PROTECTED; 
    } else if (namespaceKind == AbcNamespaceKind.Namespace()) {      
      return Visibility.UNKNOWN; 
    } 
    return Visibility.PUBLIC;
  }
  
  private java.util.List<Member> getMembers(AbcTraitClass traitClass) {
    java.util.List<Member> result = new ArrayList<Member>();

    AbcClass klass = traitClass.nominalType().klass();
    AbcInstance abcInstance = traitClass.nominalType().inst();

    // Static
    AbcTrait[] staticTraits = klass.traits();
    for (AbcTrait staticTrait : staticTraits) {
      extractMemberName(staticTrait, result, true);
    }

    // Instance
    AbcTrait[] instanceTraits = abcInstance.traits();
    for (AbcTrait instanceTrait : instanceTraits) {
      extractMemberName(instanceTrait, result, false);
    }

    return result;
  }

  private Document getOrCreateDocument(String packName) {
    if (digestsMap.containsKey(packName)) {
      return digestsMap.get(packName);
    }

    Document document = XMLUtils.createDocument();
    Element rootElement = document.createElement("digest");
    rootElement.setAttribute("package", packName);
    document.appendChild(rootElement);

    digestsMap.put(packName, document);

    return document;
  }

}

