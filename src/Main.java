import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {
        SubClass sub = new SubClass();

        sub.doSomething();
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