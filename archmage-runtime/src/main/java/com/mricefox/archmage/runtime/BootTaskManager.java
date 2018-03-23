package com.mricefox.archmage.runtime;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.mricefox.archmage.runtime.Utils.checkNull;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:Keep mappings of alias to boot node, sort boot node's alias
 * <p>Date:2018/1/5
 */

/*package*/ class BootTaskManager<Node extends BootNode, Alias> extends DAG<Class<? extends Alias>> {
    private final Logger logger = Logger.getLogger(BootTaskManager.class);
    private final DependencyLookupHook dlHook = ArchmageAspectPlugins.inst().getDependencyLookupHook();

    //alias to boot node mappings
    protected final Map<Class<? extends Alias>, Node> aliasToBootNode = new LinkedHashMap<>();
    //Sorted boot nodes after flatten dependencies
    protected final LinkedList<Node> flattenDependencies = new LinkedList<>();
    protected boolean flattened = false;

    void addDependencyNode(Class<? extends Alias> alias, Node node) {
        checkNull(node, "Node");
        checkNull(alias, "Alias");
        checkAlias(alias);

        logger.info("addDependencyNode alias:" + alias + " node:" + node);

        if (aliasToBootNode.containsKey(alias)) {
            throw new IllegalArgumentException("Duplicate alias:" + alias);
        }

        if (aliasToBootNode.containsValue(node)) {
            throw new IllegalArgumentException("Duplicate node:" + node);
        }

        aliasToBootNode.put(alias, node);

        if (!containsNode(alias)) {
            addNode(alias);
        }
    }

    void addDependency(Class<? extends Alias> node, final Class<? extends Alias> dependency) {
        checkNull(node, "Node");
        checkNull(dependency, "Dependency");
        checkAlias(node);
        checkAlias(dependency);

        if (!containsNode(node)) {
            addNode(node);
        }

        if (!containsNode(dependency)) {
            addNode(dependency);
        }

        addEdge(dependency, node);
    }

    void flattenDependency() {
        logger.info("raw elements:" + getNodes());

        checkDependencies();

        if (!flattenDependencies.isEmpty()) {
            logger.warn("Resolve more than once");
            flattenDependencies.clear();
        }
        LinkedList<Class<? extends Alias>> sorted = topologicalSorting();

        for (Class<? extends Alias> alias : sorted) {
            logger.info("flattenDependencies.add:" + aliasToBootNode.get(alias) + " alias:" + alias);
            flattenDependencies.add(aliasToBootNode.get(alias));
        }

        flattened = true;

        logger.info("flattenDependencies:" + flattenDependencies);
    }

    Snapshot<Node> rawNodesSnapshot() {
        return new Snapshot<Node>(aliasToBootNode.values());
    }

    Snapshot<Node> flattenNodesSnapshot() {
        if (!flattened) {
            throw new ArchmageException("Should call flattenDependency() first");
        }
        return new Snapshot<Node>(flattenDependencies);
    }

    /**
     * BootTask's default alias is this.class if method alias() is not override, when you add anonymous
     * class to task manager, its alias will looks like com.mricefox.sample.SomeModule$1.
     * <p>After or before that, you invoked dependsOn(InitTask.class) to construct dependency relation,
     * the alias InitTask.class which is different with com.mricefox.sample.SomeModule$1
     * <p>The wrong code looks like,
     * <pre> {@code
     *     //Declare boot task,but did not override method alias()
     *     public class InitTask extends LightBootTask {
     *          public void boot(Application application, Bundle extra) {
     *              //...
     *         }
     *     }
     *
     *     public class SomeModule extends ArchmageModule {
     *          public void declareBootDependency() {
     *              //register alias com.mricefox.sample.InitTask.class
     *              dependsOn(InitTask.class);
     *
     *              addLightBootTask(
     *                  //anonymous class which alias is com.mricefox.sample.SomeModule$1
     *                  new InitTask() {};
     *              );
     *          }
     *     }
     *
     * }</pre>
     */
    private void checkAlias(Class<? extends Alias> alias) {
        if (alias.isAnonymousClass()) {
            throw new IllegalArgumentException("Alias should not be anonymous class");
        }
    }

    private void checkDependencies() {
        List<Class<? extends Alias>> nodes = getNodes();

        for (Class<? extends Alias> node : nodes) {
            if (aliasToBootNode.get(node) == null) {
                dlHook.onBootTaskNotFound(node);
                throw new BootTaskNotFoundException("Boot task with alias:" + node + " not found");
            }
        }
    }
}
