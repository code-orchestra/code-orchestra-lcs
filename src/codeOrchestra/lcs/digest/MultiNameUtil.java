package codeOrchestra.lcs.digest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import codeOrchestra.utils.StringUtils;

import apparat.abc.AbcMultiname;
import apparat.abc.AbcName;
import apparat.abc.AbcNamespace;
import apparat.abc.AbcNamespaceKind;
import apparat.abc.AbcQName;

public class MultiNameUtil {

  public static String getName(AbcName name) {
    if (name instanceof AbcQName) {
      return ((AbcQName) name).name().name;
    } else if (name instanceof AbcMultiname) {
      return ((AbcMultiname) name).name().name;
    } else {
      return null;
    }
  }

  public static String getNamespace(AbcName name) {
    if (name instanceof AbcQName) {
      return ((AbcQName) name).namespace().name().name;
    } else if (name instanceof AbcMultiname) {
      boolean emptyNamespace = false;

      List<AbcNamespace> nameList = new ArrayList<AbcNamespace>();
      AbcNamespace[] namespaces = ((AbcMultiname) name).nsset().set();
      for (AbcNamespace namespace : namespaces) {
        nameList.add(namespace);
      }

      Collections.reverse(nameList);
      for (AbcNamespace ns : nameList) {
        int nsKind = ns.kind();

        if (nsKind == AbcNamespaceKind.Private() || nsKind == AbcNamespaceKind.PackageInternal() || nsKind == AbcNamespaceKind.Protected() || nsKind == AbcNamespaceKind.Namespace()) {
          continue;
        }
        if (StringUtils.isNotEmpty(ns.name().name)) {
          return ns.name().name;
        } else {
          emptyNamespace = true;
        }
      }

      Collections.reverse(nameList);
      for (AbcNamespace ns : nameList) {
        int nsKind = ns.kind();
        if (nsKind == AbcNamespaceKind.PackageInternal()) {
          if (StringUtils.isNotEmpty(ns.name().name)) {
            return ns.name().name;
          } else {
            emptyNamespace = true;
          }
        }
        continue;
      }

      if (!(emptyNamespace)) {
        return null;
      } else {
        return "";
      }
    } else {
      return null;
    }
  }
  
  
  
}
