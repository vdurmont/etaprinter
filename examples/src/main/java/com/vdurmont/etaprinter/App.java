package com.vdurmont.etaprinter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    public static void main(String[] args) {
        List<Banana> bananas = generateBananas(1000);
        Gorilla gorilla = new Gorilla();
        int index = 0;

        ETAPrinter printer = ETAPrinter.init("bananas", bananas.size());
        while (index < bananas.size()) {
            gorilla.eatBananas(bananas.subList(index, index + 10));
            index += 10;
            printer.update(10);
        }

        printer.close();
    }

    private static List<Banana> generateBananas(int number) {
        Random random = new Random();
        List<Banana> bananas = new ArrayList<>(number);
        for (int i = 0; i < number; i++) {
            int bananaDuration = random.nextInt(500);
            bananas.add(new Banana(bananaDuration));
        }
        return bananas;
    }

    public static class Banana {
        /**
         * How many time does it take to eat this banana?
         */
        private final int duration;

        public Banana(int bananaDuration) {
            this.duration = bananaDuration;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class Gorilla {
        public void eatBananas(List<Banana> bananas) {
            bananas.forEach(this::eatBanana);
        }

        private void eatBanana(Banana banana) {
            try {
                Thread.sleep(banana.getDuration());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
