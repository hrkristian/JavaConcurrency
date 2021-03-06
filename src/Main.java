import javax.swing.plaf.nimbus.State;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        TheFinalClass finalClass = new TheFinalClass();

        StateClass newState = finalClass.getState();

        newState.value = 222;

        System.out.println(finalClass.getState().value);
    }
}

class TheFinalClass {
    private StateClass state;

    TheFinalClass() {
        state = new StateClass(111);
    }

    StateClass getState() {
        return state;
    }

}

class StateClass {
    int value;
    StateClass(int value) {
        this.value = value;
    }
}

class SuperClass {
    protected int count = 0;
    synchronized void doSomething() {
        for (int i = 0; i < 200000; i++)
            count++;

        System.out.println("Super says hello");
    }
}
class SubClass extends SuperClass {
    @Override
    synchronized void doSomething() {
        System.out.println("Sub says hello once");

        new Thread(()->{
            super.doSomething();
        }).start();

        for (int i = 0; i < 200000; i++)
            count++;

        System.out.println("Sub says hello twice");
    }
}