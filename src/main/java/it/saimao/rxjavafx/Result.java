package it.saimao.rxjavafx;

public record Result(String name, long time) {

    @Override
    public String toString() {
        return name;
    }
}
