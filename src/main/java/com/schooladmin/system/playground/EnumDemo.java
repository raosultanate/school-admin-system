package com.schooladmin.system.playground;

import com.schooladmin.system.domain.AccessLevel;
import com.schooladmin.system.domain.Department;
import com.schooladmin.system.domain.HasLabel;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * Standalone demo (no Spring) of enums: fields/methods, {@code switch}, an enum
 * implementing an interface, and {@code EnumMap}/{@code EnumSet}. Run directly:
 *
 * <pre>{@code
 * java -cp target/classes com.schooladmin.system.playground.EnumDemo
 * }</pre>
 */
public class EnumDemo {

    public static void main(String[] args) {
        demonstrateValuesAndFields();
        demonstrateClassicSwitch();
        demonstrateSwitchExpression();
        demonstrateInterfacePolymorphism();
        demonstrateEnumCollections();
    }

    private static void demonstrateValuesAndFields() {
        // values() is generated automatically for every enum -- returns all constants, in
        // the order they were declared.
        for (AccessLevel level : AccessLevel.values()) {
            System.out.println(level + " (rank " + level.getRank() + "): " + level.label());
        }
    }

    private static void demonstrateClassicSwitch() {
        // Traditional colon-based switch. Note there's no AccessLevel. prefix on the case
        // labels -- inside a switch over an enum, Java already knows the type, so just the
        // constant name is used.
        for (AccessLevel level : AccessLevel.values()) {
            String note;
            switch (level) {
                case SUPER_ADMIN:
                    note = "can do anything";
                    break;
                case ADMIN:
                    note = "day-to-day management";
                    break;
                case SUPPORT:
                    note = "read-only";
                    break;
                default:
                    // Defensive: if a new AccessLevel constant is ever added and this
                    // switch isn't updated, fail loudly here instead of silently leaving
                    // "note" with a wrong/stale value.
                    throw new IllegalStateException("Unhandled AccessLevel: " + level);
            }
            System.out.println("[classic] " + level + " -> " + note);
        }
    }

    private static void demonstrateSwitchExpression() {
        // Modern arrow-based switch EXPRESSION (Java 14+). Two real differences from the
        // classic version above: it directly produces a value (no "note;" + assignment in
        // each branch, no "break"), and the compiler checks exhaustiveness -- delete one of
        // the three cases below and this method itself fails to compile, no default needed.
        for (AccessLevel level : AccessLevel.values()) {
            String note = switch (level) {
                case SUPER_ADMIN -> "can do anything";
                case ADMIN -> "day-to-day management";
                case SUPPORT -> "read-only";
            };
            System.out.println("[expression] " + level + " -> " + note);
        }
    }

    private static void demonstrateInterfacePolymorphism() {
        // AccessLevel and Department share nothing except both implementing HasLabel --
        // yet the same method call works on both. Same underlying mechanism as Module 1's
        // Person polymorphism, just via an interface instead of an abstract class.
        printLabel(AccessLevel.ADMIN);
        printLabel(Department.COMPUTER_SCIENCE);
    }

    private static void printLabel(HasLabel item) {
        System.out.println("Label: " + item.label());
    }

    private static void demonstrateEnumCollections() {
        // EnumMap: a Map implementation specifically for enum keys. Internally backed by an
        // array indexed by the enum constant's declaration order, not a hash table -- faster
        // and more memory-compact than HashMap for this specific case. Module 4 covers Map
        // in general; this is just a preview that it exists.
        Map<AccessLevel, Integer> userCountByLevel = new EnumMap<>(AccessLevel.class);
        userCountByLevel.put(AccessLevel.SUPER_ADMIN, 1);
        userCountByLevel.put(AccessLevel.ADMIN, 4);
        System.out.println("EnumMap: " + userCountByLevel);

        // EnumSet: same array-backed efficiency idea, for a Set of enum values.
        EnumSet<AccessLevel> elevated = EnumSet.of(AccessLevel.SUPER_ADMIN, AccessLevel.ADMIN);
        System.out.println("EnumSet: " + elevated);
    }
}
